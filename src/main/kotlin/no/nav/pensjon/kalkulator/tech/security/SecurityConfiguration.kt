package no.nav.pensjon.kalkulator.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import no.nav.pensjon.kalkulator.tech.security.ingress.*
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.util.StringUtils.hasLength

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun filterChain(
        http: HttpSecurity,
        securityContextEnricher: SecurityContextEnricher,
        impersonalAccessFilter: ImpersonalAccessFilter,
        @Value("\${idporten.issuer}") personalIssuerUri1: String,
        @Value("\${idporten.audience}") personalAudience1: String,
        @Value("\${token.x.issuer}") personalIssuerUri2: String,
        @Value("\${token.x.client.id}") personalAudience2: String,
        @Value("\${azure.openid.config.issuer}") impersonalIssuerUri: String,
        @Value("\${azure.app.client.id}") impersonalAudience: String,
        @Value("\${request-matcher.internal}") internalRequestMatcher: String
    ): SecurityFilterChain {
        http.addFilterAfter(AuthenticationEnricherFilter(securityContextEnricher), BasicAuthenticationFilter::class.java)
            .addFilterAfter(impersonalAccessFilter, AuthenticationEnricherFilter::class.java)

        val resolver = tokenAuthenticationManagerResolver(
            if (hasLength(personalIssuerUri1)) personalIssuerUri1 else personalIssuerUri2,
            if (hasLength(personalAudience1)) personalAudience1 else personalAudience2,
            impersonalIssuerUri,
            impersonalAudience
        )

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
            .oauth2ResourceServer { it.authenticationManagerResolver(resolver) }
            .build()
    }

    companion object {

        /**
         * "Impersonal" means that the logged-in user acts on behalf of another person
         */
        fun isImpersonal(request: HttpServletRequest): Boolean =
            hasLength(request.getHeader(CustomHttpHeaders.PID))

        private fun tokenAuthenticationManagerResolver(
            personalIssuerUri: String,
            personalAudience: String,
            impersonalIssuerUri: String,
            impersonalAudience: String
        ): AuthenticationManagerResolver<HttpServletRequest> {
            val personalJwt = providerManager(personalIssuerUri, personalAudience)
            val impersonalJwt = providerManager(impersonalIssuerUri, impersonalAudience)
            return AuthenticationManagerResolver { if (isImpersonal(it)) impersonalJwt else personalJwt }
        }

        private fun providerManager(issuerUri: String, audience: String) =
            ProviderManager(JwtAuthenticationProvider(jwtDecoder(issuerUri, audience)))

        private fun jwtDecoder(issuerUri: String, audience: String): JwtDecoder {
            val decoder = JwtDecoders.fromIssuerLocation(issuerUri) as NimbusJwtDecoder
            val issuerValidator: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuerUri)
            decoder.setJwtValidator(DelegatingOAuth2TokenValidator(issuerValidator, AudienceValidator(audience)))
            return decoder
        }
    }
}
