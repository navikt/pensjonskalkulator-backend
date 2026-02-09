package no.nav.pensjon.kalkulator.tech.security

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.tech.security.egress.SecurityContextEnricher
import no.nav.pensjon.kalkulator.tech.security.ingress.AudienceValidator
import no.nav.pensjon.kalkulator.tech.security.ingress.AuthenticationEnricherFilter
import no.nav.pensjon.kalkulator.tech.security.ingress.LoggingAuthenticationEntryPoint
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityLevelFilter
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
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.ShadowTilgangComparator
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.util.StringUtils.hasLength

@Configuration
@EnableWebSecurity
class SecurityConfiguration(private val requestClaimExtractor: RequestClaimExtractor) {

    @Bean("personal")
    @Primary
    fun personalProviderManager(
        @Qualifier("token-x-provider") tokenXProvider: AuthenticationProvider
    ) =
        ProviderManager(tokenXProvider)

    @Bean("impersonal")
    fun impersonalProviderManager(
        @Qualifier("entra-id-provider") provider: AuthenticationProvider
    ) =
        ProviderManager(provider)

    @Bean("universal")
    fun universalProviderManager(
        @Qualifier("token-x-provider") tokenXProvider: AuthenticationProvider,
        @Qualifier("entra-id-provider") entraIdProvider: AuthenticationProvider
    ) =
        ProviderManager(tokenXProvider, entraIdProvider)

    @Bean("token-x-provider")
    @Primary
    fun tokenXProvider(
        @Value("\${token-x.issuer}") issuer: String,
        @Value("\${token-x.client.id}") audience: String
    ) =
        JwtAuthenticationProvider(jwtDecoder(issuer, audience))

    @Bean("entra-id-provider")
    fun entraIdProvider(
        @Value("\${azure.openid.config.issuer}") issuer: String,
        @Value("\${azure-app.client-id}") audience: String
    ) =
        JwtAuthenticationProvider(jwtDecoder(issuer, audience))

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
        pidExtractor: PidExtractor,
        pidGetter: PidGetter,
        auditor: Auditor,
        adresseService: FortroligAdresseService,
        groupMembershipService: GroupMembershipService,
        shadowTilgangComparator: ShadowTilgangComparator,
        authResolver: AuthenticationManagerResolver<HttpServletRequest>,
        authenticationEntryPoint: LoggingAuthenticationEntryPoint,
        @Value("\${pkb.request-matcher.internal}") internalRequestMatcher: String
    ): SecurityFilterChain {
        http.addFilterAfter(
            AuthenticationEnricherFilter(securityContextEnricher),
            BasicAuthenticationFilter::class.java
        )
            .addFilterAfter(
                ImpersonalAccessFilter(pidExtractor, groupMembershipService, auditor, shadowTilgangComparator),
                AuthenticationEnricherFilter::class.java
            )
            .addFilterAfter(
                SecurityLevelFilter(adresseService, pidGetter),
                ImpersonalAccessFilter::class.java
            )

        return http.csrf {
            it.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            it.csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
        }
            .authorizeHttpRequests {
                it.requestMatchers(
                    HttpMethod.GET,
                    internalRequestMatcher,
                    "/api/csrf",
                    "/api/status",
                    "/api/v1/land-liste",
                    "/api/feature/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/error"
                ).permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/error"
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
        request.requestURI == ANONYM_SIMULERING_URI
                || request.requestURI == ALDERSGRENSE_URI
                || request.requestURI == ANSATT_ID_URI && hasAnsattIdClaim(request)
                || hasPidHeader(request)

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
        const val FEATURE_URI = "/api/feature/"
        private const val ANONYM_SIMULERING_URI = "/api/v1/alderspensjon/anonym-simulering"
        private const val ALDERSGRENSE_URI = "/api/v1/aldersgrense"
        private const val ANSATT_ID_URI = "/api/v1/ansatt-id"
        private const val ENCRYPTION_URI = "/api/v1/encrypt"

        private fun hasPidHeader(request: HttpServletRequest): Boolean =
            hasLength(request.getHeader(CustomHttpHeaders.PID))

        private fun jwtDecoder(issuer: String, audience: String): JwtDecoder =
            decoder(issuer).apply {
                setJwtValidator(
                    DelegatingOAuth2TokenValidator(
                        JwtValidators.createDefaultWithIssuer(issuer),
                        AudienceValidator(audience)
                    )
                )
            }

        private fun decoder(issuer: String) =
            JwtDecoders.fromIssuerLocation(issuer) as NimbusJwtDecoder
    }
}
