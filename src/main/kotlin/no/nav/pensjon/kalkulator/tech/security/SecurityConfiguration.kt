package no.nav.pensjon.kalkulator.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import no.nav.pensjon.kalkulator.tech.security.ingress.AudienceValidator
import no.nav.pensjon.kalkulator.tech.security.ingress.AuthenticationEnricherFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.LoggingAuthenticationEntryPoint
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtDecoders
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.util.StringUtils.hasLength

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    /**
     * Supports two issuer configurations (ID-porten and TokenX) at bean instantiation time,
     * but only one of them will be used during call processing.
     * -----
     * Tokens issued for frontend are accepted as well as tokens issued for backend.
     * This is in order to support both these scenarios:
     * - Frontend forwards Wonderwall token to backend without exchanging it
     * - Frontend exchanges Wonderwall token into TokenX or Entra ID OBO token, and uses the latter when calling backend
     */
    @Bean("personal")
    @Primary
    fun personalProviderManager(
        @Value("\${idporten.issuer}") idPortenIssuerUri: String,
        @Value("\${idporten.audience}") idPortenAudience: String,
        @Value("\${token.x.issuer}") tokenXIssuerUri: String,
        @Value("\${token.x.client.id}") tokenXAudience: String
    ) =
        ProviderManager(
            JwtAuthenticationProvider(
                jwtDecoder(
                    issuerUri = if (hasLength(idPortenIssuerUri)) idPortenIssuerUri else tokenXIssuerUri,
                    frontendAudiences = listOf(idPortenAudience),
                    backendAudience = tokenXAudience
                )
            )
        )

    @Bean("impersonal")
    fun impersonalProviderManager(
        @Value("\${azure.openid.config.issuer}") issuerUri: String,
        @Value("\${pkb.frontend.entra.client.id}") frontendAudiences: String,
        @Value("\${azure-app.client-id}") backendAudience: String
    ) = ProviderManager(JwtAuthenticationProvider(jwtDecoder(issuerUri, frontendAudiences.split(","), backendAudience)))

    @Bean
    fun tokenAuthenticationManagerResolver(
        @Qualifier("personal") personalProviderManager: ProviderManager,
        @Qualifier("impersonal") impersonalProviderManager: ProviderManager
    ): AuthenticationManagerResolver<HttpServletRequest> =
        AuthenticationManagerResolver { if (isImpersonal(it)) impersonalProviderManager else personalProviderManager }

    @Bean
    fun filterChain(
        http: HttpSecurity,
        securityContextEnricher: SecurityContextEnricher,
        impersonalAccessFilter: ImpersonalAccessFilter,
        authResolver: AuthenticationManagerResolver<HttpServletRequest>,
        authenticationEntryPoint: LoggingAuthenticationEntryPoint,
        @Value("\${pkb.request-matcher.internal}") internalRequestMatcher: String
    ): SecurityFilterChain {
        http.addFilterAfter(
            AuthenticationEnricherFilter(securityContextEnricher),
            BasicAuthenticationFilter::class.java
        )
            .addFilterAfter(impersonalAccessFilter, AuthenticationEnricherFilter::class.java)

        return http
            .authorizeHttpRequests {
                it.requestMatchers(
                    HttpMethod.GET,
                    internalRequestMatcher,
                    "/error",
                    "/api/status",
                    "/api/feature/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.authenticationManagerResolver(authResolver)
                    .authenticationEntryPoint(authenticationEntryPoint)
            }
            .build()
    }

    companion object {

        /**
         * "Impersonal" means that the logged-in user acts on behalf of another person
         */
        fun isImpersonal(request: HttpServletRequest): Boolean =
            hasLength(request.getHeader(CustomHttpHeaders.PID))

        private fun jwtDecoder(issuerUri: String, frontendAudiences: List<String>, backendAudience: String): JwtDecoder {
            val decoder = JwtDecoders.fromIssuerLocation(issuerUri) as NimbusJwtDecoder

            decoder.setJwtValidator(DelegatingOAuth2TokenValidator(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                AudienceValidator(frontendAudiences, backendAudience)))

            return decoder
        }
    }
}
