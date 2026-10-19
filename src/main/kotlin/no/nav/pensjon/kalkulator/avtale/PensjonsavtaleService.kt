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
            filterAndPopulateUnavailable(mockClient.hent(spec))
        } else {
            filterAndPopulateUnavailable(client.fetchAvtaler(spec))
        }
    }

    private companion object {

        private fun filterAndPopulateUnavailable(avtaler: Pensjonsavtaler): Pensjonsavtaler {
            val avtalerMedManglendeBeregning = utilgjengeligeAvtalerPgaManglendeBeregning(avtaler)
            return Pensjonsavtaler(
                avtaler = avtaler.avtaler
                    .filter { it.kategori.included && it.harStartAar }
                    .filter { it !in avtalerMedManglendeBeregning },
                utilgjengeligeSelskap = avtaler.utilgjengeligeSelskap
                            + avtalerMedManglendeBeregning.map { Selskap(it.selskapsnavn, true) }
            )
        }

        private fun utilgjengeligeAvtalerPgaManglendeBeregning(avtaler: Pensjonsavtaler) =
            avtaler.avtaler.filter {
                it.utbetalingsperioder.isEmpty() && it.manglendeBeregningAarsak !in listOf(
                    ManglendeEksternBeregningAarsak.NONE,
                    ManglendeEksternBeregningAarsak.UNKNOWN
                )
            }
    }
}
