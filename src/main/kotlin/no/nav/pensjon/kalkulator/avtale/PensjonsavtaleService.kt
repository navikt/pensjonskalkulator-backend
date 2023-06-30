package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    private val avtaleClient: PensjonsavtaleClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    fun fetchAvtaler(spec: PensjonsavtaleSpecDto): Pensjonsavtaler {
        return if (featureToggleService.isEnabled("pensjonskalkulator.mock-norsk-pensjon"))
            mockAvtaler(spec)
        else
            avtaleClient.fetchAvtaler(fromDto(spec))
    }

    private fun fromDto(dto: PensjonsavtaleSpecDto) =
        PensjonsavtaleSpec(
            pidGetter.pid(),
            dto.aarligInntektFoerUttak,
            dto.uttaksperioder,
            dto.antallInntektsaarEtterUttak
        )

    private companion object {

        /**
         * Temporary function for testing pensjonsavtaler with synthetic persons
         * (per June 2023 Norsk Pensjon does not support synthetic persons)
         */
        private fun mockAvtaler(spec: PensjonsavtaleSpecDto): Pensjonsavtaler {
            val uttaksperiode = if (spec.uttaksperioder.isEmpty()) UttaksperiodeSpec(67, 1, 100, 10000) else spec.uttaksperioder[0]
            val startAlder = uttaksperiode.startAlder
            val someNumber = System.currentTimeMillis().toString().substring(7).toInt()
            val startMaaned = someNumber % 12 + 1
            val sluttMaaned = (someNumber + startAlder) % 12 + 1

            return Pensjonsavtaler(
                listOf(
                    Pensjonsavtale(
                        "PENSJONSKAPITALBEVIS",
                        "innskuddsbasertKollektiv",
                        startAlder,
                        startAlder + 10,
                        listOf(
                            Utbetalingsperiode(
                                Alder(startAlder, startMaaned),
                                Alder(startAlder + 10, sluttMaaned),
                                someNumber,
                                uttaksperiode.grad
                            )
                        )
                    )
                ), emptyList()
            )
        }
    }
}
