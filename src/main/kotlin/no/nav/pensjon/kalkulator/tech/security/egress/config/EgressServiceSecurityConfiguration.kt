package no.nav.pensjon.kalkulator.tech.security.egress.config

import no.nav.pensjon.kalkulator.tech.security.egress.AuthType
import no.nav.pensjon.kalkulator.tech.security.egress.token.EgressAccessTokenFacade
import no.nav.pensjon.kalkulator.tech.security.egress.token.RawJwt
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.function.Function

@Configuration
class EgressServiceSecurityConfiguration {

    @Bean
    fun egressServiceListsByAudience(
        @Value("\${pen.service-id}") pensjonsfagligKjerneServiceId: String,
        @Value("\${popp.service-id}") pensjonsopptjeningServiceId: String,
        @Value("\${pensjon-pid-encryption.service-id}") pensjonPidEncryptionServiceId: String,
        @Value("\${pensjon-representasjon.service-id}") pensjonRepresentasjonServiceId: String,
        @Value("\${pensjonssimulator.service-id}") pensjonssimulatorServiceId: String,
        @Value("\${persondata.service-id}") persondataServiceId: String,
        @Value("\${skjermede-personer.service-id}") skjermedePersonerServiceId: String,
        @Value("\${tjenestepensjon.service-id}") tjenestepensjonServiceId: String,
        @Value("\${proxy.service-id}") proxyServiceId: String,
        @Value("\${omstillingsstoenad.service-id}") omstillingsstoenadServiceId: String,
        @Value("\${sokos.utbetaldata.service-id}") utbetalingDataServiceId: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.aksio.scope}") aksioScope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.gabler.scope}") gablerScope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.gabler2.scope}") gabler2Scope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.klp.scope}") klpScope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.opf.scope}") opfScope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.spk.scope}") spkScope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.storebrand.scope}") storebrandLivsforsikringScope: String,
        @Value("\${livsvarig-afp-offentlig.tilbydere.storebrand_pen.scope}") storebrandPensjonstjenesterScope: String,
        @Value("\${tilgangsmaskinen.service-id}") tilgangsmaskinenServiceId: String
    ) =
        EgressServiceListsByAudience(
            mapOf(
                pensjonsfagligKjerneServiceId to listOf(EgressService.PENSJONSFAGLIG_KJERNE),
                pensjonsopptjeningServiceId to listOf(EgressService.PENSJONSOPPTJENING),
                pensjonPidEncryptionServiceId to listOf(EgressService.PENSJON_PID_ENCRYPTION),
                pensjonRepresentasjonServiceId to listOf(EgressService.PENSJON_REPRESENTASJON),
                pensjonssimulatorServiceId to listOf(EgressService.PENSJONSSIMULATOR),
                persondataServiceId to listOf(EgressService.PERSONDATALOESNINGEN),
                skjermedePersonerServiceId to listOf(EgressService.SKJERMEDE_PERSONER),
                tjenestepensjonServiceId to listOf(EgressService.TJENESTEPENSJON),
                proxyServiceId to EgressService.servicesAccessibleViaProxy,
                omstillingsstoenadServiceId to listOf(EgressService.OMSTILLINGSSTOENAD),
                utbetalingDataServiceId to listOf(EgressService.UTBETALING_DATA),
                aksioScope to listOf(EgressService.AKSIO),
                gablerScope to listOf(EgressService.GABLER),
                gabler2Scope to listOf(EgressService.GABLER2),
                klpScope to listOf(EgressService.KLP),
                opfScope to listOf(EgressService.OPF),
                spkScope to listOf(EgressService.SPK),
                storebrandLivsforsikringScope to listOf(EgressService.STOREBRAND_LIVSFORSIKRING),
                storebrandPensjonstjenesterScope to listOf(EgressService.STOREBRAND_PENSJONSTJENESTER),
                tilgangsmaskinenServiceId to listOf(EgressService.TILGANGSMASKINEN)
            )
        )

    @Bean
    fun egressTokenSuppliersByService(
        serviceListsByAudience: EgressServiceListsByAudience,
        egressTokenGetter: EgressAccessTokenFacade
    ): EgressTokenSuppliersByService {
        val suppliersByService: MutableMap<EgressService, Function<String?, RawJwt>> =
            EnumMap(EgressService::class.java)

        serviceListsByAudience.entries.forEach { (audience, services) ->
            obtainTokenSupplier(
                audience,
                services,
                authType = services.first().authType,
                egressTokenGetter,
                tokenSuppliersByService = suppliersByService
            )
        }

        return EgressTokenSuppliersByService(suppliersByService)
    }

    companion object {

        private fun obtainTokenSupplier(
            audience: String,
            services: List<EgressService>,
            authType: AuthType,
            egressTokenGetter: EgressAccessTokenFacade,
            tokenSuppliersByService: MutableMap<EgressService, Function<String?, RawJwt>>
        ) {
            val tokenSupplier = Function<String?, RawJwt> {
                egressTokenGetter.getAccessToken(authType, audience, ingressToken = it)
            }

            services.forEach { tokenSuppliersByService[it] = tokenSupplier }
        }
    }
}
