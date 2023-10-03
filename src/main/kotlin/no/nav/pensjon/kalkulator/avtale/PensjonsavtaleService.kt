package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid
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
        return if (featureToggleService.isEnabled("mock-norsk-pensjon") && pidGetter.pid() == Pid("02817996259"))
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

        /**
         * Temporary function for testing pensjonsavtaler with start before uttaksalder
         */
        private fun mockAvtaler(): Pensjonsavtaler {
            val startAlderAar = 57

            return Pensjonsavtaler(
                listOf(
                    Pensjonsavtale(
                        "PENSJONSKAPITALBEVIS",
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
    }
}
