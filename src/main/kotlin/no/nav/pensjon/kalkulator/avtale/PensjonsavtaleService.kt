package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    private val avtaleClient: PensjonsavtaleClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
        val fnr = pidGetter.pid().value

        return if (featureToggleService.isEnabled("mock-norsk-pensjon") && mockFnrs.contains(fnr))
            if (fnr == "46918903739")
                mockGjensidigeAvtaler()
            else
                mockAvtaler()
        else
            filter(avtaleClient.fetchAvtaler(spec, pidGetter.pid()))
    }

    private companion object {

        private fun filter(avtaler: Pensjonsavtaler) =
            Pensjonsavtaler(
                avtaler = avtaler.avtaler.filter { it.kategori.included },
                utilgjengeligeSelskap = avtaler.utilgjengeligeSelskap
            )

        private val mockFnrs = listOf("46918903739", "02817996259")

        /**
         * Temporary function for testing pensjonsavtaler with start before uttaksalder
         */
        private fun mockAvtaler(): Pensjonsavtaler {
            val startAlderAar = 57

            return Pensjonsavtaler(
                listOf(
                    Pensjonsavtale(
                        "Mock livsvarig individuell ordning",
                        AvtaleKategori.INDIVIDUELL_ORDNING,
                        startAlderAar,
                        null,
                        listOf(
                            Utbetalingsperiode(
                                startAlder = Alder(startAlderAar, 0),
                                null,
                                aarligUtbetaling = 32001,
                                grad = Uttaksgrad.HUNDRE_PROSENT
                            )
                        )
                    )
                ), emptyList()
            )
        }

        /**
         * Temporary function for testing pensjonsavtaler with start/end maaneder not 0
         */
        private fun mockGjensidigeAvtaler(): Pensjonsavtaler {
            val startAlderAar = 66
            val startAlderMaaneder = 6

            return Pensjonsavtaler(
                listOf(
                    Pensjonsavtale(
                        "Mock privat tjenestepensjon",
                        AvtaleKategori.PRIVAT_TJENESTEPENSJON,
                        startAlderAar,
                        null,
                        listOf(
                            Utbetalingsperiode(
                                startAlder = Alder(startAlderAar, startAlderMaaneder),
                                sluttAlder = Alder(startAlderAar + 10, startAlderMaaneder),
                                aarligUtbetaling = 29008,
                                grad = Uttaksgrad.HUNDRE_PROSENT
                            )
                        )
                    )
                ), emptyList()
            )
        }
    }
}
