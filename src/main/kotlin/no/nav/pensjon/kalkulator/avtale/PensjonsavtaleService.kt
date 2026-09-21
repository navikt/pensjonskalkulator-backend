package no.nav.pensjon.kalkulator.avtale

import jakarta.annotation.PreDestroy
import mu.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.cancellation.CancellationException

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
    private val comparisonCounter = AtomicInteger()

    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        return if (featureToggleService.isEnabled("mock-norsk-pensjon")) {
            filter(mockAvtaleClient.fetchAvtaler(spec, pidGetter.pid()))
        } else if (featureToggleService.isEnabled("norsk-pensjon-via-rest")) {
            filter(avtaleClient.fetchAvtaler(spec, pidGetter.pid()))
        } else if (featureToggleService.isEnabled("norsk-pensjon-compare-rest-and-soap")) {
            log.info { "Comparing pensjonsavtaler from SOAP and REST for spec: $spec" }
            val avtalerFraSoap = filter(avtaleClientSoap.fetchAvtaler(spec, pidGetter.pid()))

            compareAvtalerAsync(spec = spec, avtalerFraSoap = avtalerFraSoap, pid = pidGetter.pid())

            avtalerFraSoap
        } else {
            filter(avtaleClientSoap.fetchAvtaler(spec, pidGetter.pid()))
        }
    }

    private fun compareAvtalerAsync(spec: PensjonsavtaleSpec, avtalerFraSoap: Pensjonsavtaler, pid: Pid) {
        comparisonScope.launch(SecurityCoroutineContext()) {
            try {
                val avtalerFraRest = filter(avtaleClient.fetchAvtaler(spec, pid))
                if (avtalerFraSoap != avtalerFraRest) {
                    log.warn { "Ulikheter i pensjonsavtaler fra SOAP og REST: SOAP: $avtalerFraSoap, REST: $avtalerFraRest" }
                    log.warn { "Ulikheter i pensjonsavtaler for spec: $spec" }
                } else {
                    log.warn { "Pensjonsavtaler fra SOAP og REST er like." }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                log.warn(e) { "Sammenligning mot Norsk Pensjon REST feilet, SOAP avtaler: $avtalerFraSoap" }
            }
        }
    }

    private fun shouldCompare(): Boolean =
        comparisonCounter.updateAndGet { current -> if (current == 9) 0 else current + 1 } == 0

    @PreDestroy
    fun stop() {
        comparisonScope.cancel()
    }

    private companion object {

        private fun filter(avtaler: Pensjonsavtaler) =
            Pensjonsavtaler(
                avtaler = avtaler.avtaler.filter { it.kategori.included && it.harStartAar },
                utilgjengeligeSelskap = avtaler.utilgjengeligeSelskap
            )
    }
}
