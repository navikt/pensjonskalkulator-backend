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
        return if (featureToggleService.isEnabled("mock-norsk-pensjon"))
            mockAvtaler(spec)
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
         * Temporary function for testing pensjonsavtaler with synthetic persons
         * (per June 2023 Norsk Pensjon does not support synthetic persons)
         */
        private fun mockAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler {
            val uttaksperiode =
                if (spec.uttaksperioder.isEmpty())
                    UttaksperiodeSpec(
                        Alder(67, 0),
                        Uttaksgrad.HUNDRE_PROSENT,
                        10000
                    )
                else
                    spec.uttaksperioder[0]

            val startAlderAar = uttaksperiode.startAlder.aar
            val someNumber = System.currentTimeMillis().toString().substring(7).toInt()
            val startAlderMaaneder = someNumber % 12 + 1
            val sluttAlderMaaneder = (someNumber + startAlderAar) % 12 + 1

            return Pensjonsavtaler(
                listOf(
                    Pensjonsavtale(
                        "PENSJONSKAPITALBEVIS",
                        AvtaleKategori.INDIVIDUELL_ORDNING,
                        startAlderAar,
                        startAlderAar + 10,
                        listOf(
                            Utbetalingsperiode(
                                startAlder = Alder(startAlderAar, startAlderMaaneder),
                                sluttAlder = Alder(startAlderAar + 10, sluttAlderMaaneder),
                                aarligUtbetaling = someNumber,
                                grad = uttaksperiode.grad
                            )
                        )
                    )
                ), emptyList()
            )
        }
    }
}
