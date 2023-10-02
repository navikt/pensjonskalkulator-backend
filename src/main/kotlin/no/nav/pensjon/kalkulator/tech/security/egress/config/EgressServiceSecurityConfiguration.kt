package no.nav.pensjon.kalkulator.tech.security.egress.config

import no.nav.pensjon.kalkulator.tech.security.egress.UserType
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressAccessTokenFacade
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.function.Supplier

@Configuration
class EgressServiceSecurityConfiguration {

    @Bean
    fun egressServiceListsByAudience(
        @Value("\${pensjon-regler.service-id}") pensjonReglerServiceId: String,
        @Value("\${persondata.service-id}") persondataServiceId: String,
        @Value("\${tjenestepensjon.service-id}") tjenestepensjonServiceId: String,
        @Value("\${proxy.service-id}") proxyServiceId: String
    ): EgressServiceListsByAudience {
        return EgressServiceListsByAudience(
            mapOf(
                persondataServiceId to listOf(EgressService.PERSONDATALOESNINGEN),
                tjenestepensjonServiceId to listOf(EgressService.TJENESTEPENSJON),
                proxyServiceId to EgressService.servicesAccessibleViaProxy
            )
        )
    }

    @Bean
    fun impersonalEgressTokenSuppliersByService(
        serviceListsByAudience: EgressServiceListsByAudience,
        egressTokenGetter: EgressAccessTokenFacade
    ): EgressTokenSuppliersByService {
        val suppliersByService: MutableMap<EgressService, Supplier<RawJwt>> = EnumMap(EgressService::class.java)

        serviceListsByAudience.entries.forEach { (audience, services) ->
            obtainImpersonalTokenSupplier(
                audience,
                services,
                egressTokenGetter,
                suppliersByService
            )
        }

        return EgressTokenSuppliersByService(suppliersByService)
    }

    companion object {
        private fun obtainImpersonalTokenSupplier(
            audience: String,
            services: List<EgressService>,
            egressTokenGetter: EgressAccessTokenFacade,
            tokenSuppliersByService: MutableMap<EgressService, Supplier<RawJwt>>
        ) {
            val tokenSupplier = Supplier<RawJwt> { egressTokenGetter.getAccessToken(UserType.APPLICATION, audience) }
            services.forEach { tokenSuppliersByService[it] = tokenSupplier }
        }
    }
}
