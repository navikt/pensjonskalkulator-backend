package no.nav.pensjon.kalkulator.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import no.nav.pensjon.kalkulator.tech.security.ingress.AudienceValidator
import no.nav.pensjon.kalkulator.tech.security.ingress.AuthenticationEnricherFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.LoggingAuthenticationEntryPoint
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.ImpersonalAccessFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.jwt.RequestClaimExtractor
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.AuthenticationProvider
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
class SecurityConfiguration(private val requestClaimExtractor: RequestClaimExtractor) {

    /**
     * Supports two issuer configurations (ID-porten and TokenX).
     * -----
     * Tokens issued for frontend are accepted as well as tokens issued for backend.
     * This is in order to support both these scenarios:
     * - Frontend forwards Wonderwall token to backend without exchanging it
     * - Frontend exchanges Wonderwall token into TokenX or Entra ID OBO token, and uses the latter when calling backend
     */
    @Bean("personal")
    @Primary
    fun personalProviderManager(
        @Qualifier("id-porten-provider") idPortenProvider: AuthenticationProvider,
        @Qualifier("token-x-provider") tokenXProvider: AuthenticationProvider
    ) =
        ProviderManager(idPortenProvider, tokenXProvider)

    @Bean("impersonal")
    fun impersonalProviderManager(
        @Qualifier("entra-id-provider") provider: AuthenticationProvider
    ) =
        ProviderManager(provider)

    @Bean("universal")
    fun universalProviderManager(
        @Qualifier("id-porten-provider") idPortenProvider: AuthenticationProvider,
        @Qualifier("token-x-provider") tokenXProvider: AuthenticationProvider,
        @Qualifier("entra-id-provider") entraIdProvider: AuthenticationProvider
    ) =
        ProviderManager(idPortenProvider, tokenXProvider, entraIdProvider)

    @Bean("id-porten-provider")
    @Primary
    fun idPortenProvider(
        @Value("\${idporten.issuer}") issuer: String,
        @Value("\${idporten.audience}") audience: String
    ) =
        JwtAuthenticationProvider(
            jwtDecoder(
                issuerUri = issuer,
                frontendAudiences = listOf(audience),
                backendAudience = ""
            )
        )

    @Bean("token-x-provider")
    fun tokenXProvider(
        @Value("\${token-x.issuer}") issuer: String,
        @Value("\${token-x.client.id}") audience: String
    ) =
        JwtAuthenticationProvider(
            jwtDecoder(
                issuerUri = issuer,
                frontendAudiences = emptyList(),
                backendAudience = audience
            )
        )

    @Bean("entra-id-provider")
    fun entraIdProvider(
        @Value("\${azure.openid.config.issuer}") issuer: String,
        @Value("\${pkb.frontend.entra.client.id}") frontendAudiences: String,
        @Value("\${azure-app.client-id}") backendAudience: String
    ) =
        JwtAuthenticationProvider(
            jwtDecoder(
                issuerUri = issuer,
                frontendAudiences = frontendAudiences.split(","),
                backendAudience
            )
        )

    @Bean
    fun tokenAuthenticationManagerResolver(
        @Qualifier("personal") personalProviderManager: ProviderManager,
        @Qualifier("impersonal") impersonalProviderManager: ProviderManager,
        @Qualifier("universal") universalProviderManager: ProviderManager
    ): AuthenticationManagerResolver<HttpServletRequest> =
        AuthenticationManagerResolver {
            if (isUniversal(it))
                universalProviderManager
            else
                if (isImpersonal(it))
                    impersonalProviderManager
                else
                    personalProviderManager
        }

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

    /**
     * "Impersonal" means that the logged-in user acts on behalf of another person.
     */
    private fun isImpersonal(request: HttpServletRequest): Boolean =
        request.requestURI == ANSATT_ID_URI && hasAnsattIdClaim(request) || hasPidHeader(request)

    /**
     * "Universal" means that it cannot be determined whether the request is made in a personal or impersonal
     * context. This implies that any one of the relevant token issuers must be accepted.
     */
    private fun isUniversal(request: HttpServletRequest): Boolean =
        request.requestURI == ENCRYPTION_URI ||
                request.requestURI.startsWith(FEATURE_URI)

    private fun hasAnsattIdClaim(request: HttpServletRequest): Boolean =
        hasLength(requestClaimExtractor.extractAuthorizationClaim(request, SecurityContextNavIdExtractor.CLAIM_KEY))

    companion object {
        private const val ANSATT_ID_URI = "/api/v1/ansatt-id"
        private const val FEATURE_URI = "/api/feature/"
        private const val ENCRYPTION_URI = "/api/v1/encrypt"

        fun hasPidHeader(request: HttpServletRequest): Boolean =
            hasLength(request.getHeader(CustomHttpHeaders.PID))

        private fun jwtDecoder(
            issuerUri: String,
            frontendAudiences: List<String>,
            backendAudience: String
        ): JwtDecoder {
            val decoder = JwtDecoders.fromIssuerLocation(issuerUri) as NimbusJwtDecoder

            decoder.setJwtValidator(
                DelegatingOAuth2TokenValidator(
                    JwtValidators.createDefaultWithIssuer(issuerUri),
                    AudienceValidator(frontendAudiences, backendAudience)
                )
            )

            return decoder
        }
    }
}
