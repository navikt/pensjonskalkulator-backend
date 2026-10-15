package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.mock.NorskPensjonMockPensjonsavtaleClient
import no.nav.pensjon.kalkulator.tech.env.EnvironmentUtil
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    private val client: PensjonsavtaleClient,
    private val mockClient: NorskPensjonMockPensjonsavtaleClient,
    private val featureToggleService: FeatureToggleService
) {
    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        return if (EnvironmentUtil.isDevelopment() && featureToggleService.isEnabled("mock-norsk-pensjon")) {
            filter(mockClient.hent(spec))
        } else {
            filter(client.fetchAvtaler(spec))
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
