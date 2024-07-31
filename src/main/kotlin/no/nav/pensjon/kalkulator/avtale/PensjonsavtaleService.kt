package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    @Qualifier("norskPensjon") private val avtaleClient: PensjonsavtaleClient,
    @Qualifier("norskPensjonMock") private val mockAvtaleClient: PensjonsavtaleClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        return if (featureToggleService.isEnabled("mock-norsk-pensjon"))
            filter(mockAvtaleClient.fetchAvtaler(spec, pidGetter.pid()))
        else
            filter(avtaleClient.fetchAvtaler(spec, pidGetter.pid()))
    }

    private companion object {

        private fun filter(avtaler: Pensjonsavtaler) =
            Pensjonsavtaler(
                avtaler = avtaler.avtaler.filter { it.kategori.included && it.harStartAar },
                utilgjengeligeSelskap = avtaler.utilgjengeligeSelskap
            )
    }
}
