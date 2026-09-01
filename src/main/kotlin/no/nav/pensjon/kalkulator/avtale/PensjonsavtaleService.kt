package no.nav.pensjon.kalkulator.avtale

import mu.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    @param:Qualifier("norskPensjon") private val avtaleClientSoap: PensjonsavtaleClient,
    @param:Qualifier("norsk-pensjon-rest") private val avtaleClient: PensjonsavtaleClient,
    @param:Qualifier("norskPensjonMock") private val mockAvtaleClient: PensjonsavtaleClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    private val log = KotlinLogging.logger {}
    private val comparisonScope = CoroutineScope(Dispatchers.IO)

    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        return if (featureToggleService.isEnabled("mock-norsk-pensjon"))
            filter(mockAvtaleClient.fetchAvtaler(spec, pidGetter.pid()))
        else {
//            val avtalerFraSoap = filter(avtaleClientSoap.fetchAvtaler(spec, pidGetter.pid()))
//
//            if (featureToggleService.isEnabled("compare-norsk-pensjon-via-soap-and-rest")) {
//                compareAvtalerAsync(spec = spec, avtalerFraSoap = avtalerFraSoap, pid = pidGetter.pid())
//            }
//
//            avtalerFraSoap //dev-prod
            filter(avtaleClient.fetchAvtaler(spec, pidGetter.pid())) //lokalt
        }
    }

    private fun compareAvtalerAsync(spec: PensjonsavtaleSpec, avtalerFraSoap: Pensjonsavtaler, pid: Pid) {
        comparisonScope.launch(SecurityCoroutineContext()) {
            val avtalerFraRest = filter(avtaleClient.fetchAvtaler(spec, pid))
            if (avtalerFraSoap != avtalerFraRest) {
                log.warn { "Ulikheter i pensjonsavtaler fra SOAP og REST: SOAP: $avtalerFraSoap, REST: $avtalerFraRest" }
            }
        }
    }

    private companion object {

        private fun filter(avtaler: Pensjonsavtaler) =
            Pensjonsavtaler(
                avtaler = avtaler.avtaler.filter { it.kategori.included && it.harStartAar },
                utilgjengeligeSelskap = avtaler.utilgjengeligeSelskap
            )
    }
}
