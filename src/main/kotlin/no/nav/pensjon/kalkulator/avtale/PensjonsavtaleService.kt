package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UttaksperiodeSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.Sivilstatus
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
        return if (featureToggleService.isEnabled("mock-norsk-pensjon"))
            mockAvtaler(spec)
        else
            avtaleClient.fetchAvtaler(fromDto(spec))
    }

    private fun fromDto(dto: PensjonsavtaleSpecDto) =
        PensjonsavtaleSpec(
            pidGetter.pid(),
            dto.aarligInntektFoerUttak,
            dto.uttaksperioder.map(::fromUttaksperiodeSpecDto),
            dto.antallInntektsaarEtterUttak,
            dto.harAfp ?: false,
            dto.harEpsPensjon ?: true,
            dto.harEpsPensjonsgivendeInntektOver2G ?: true,
            dto.antallAarIUtlandetEtter16 ?: 0,
            dto.sivilstatus ?: Sivilstatus.GIFT,
            dto.oenskesSimuleringAvFolketrygd ?: false
        )

    private fun fromUttaksperiodeSpecDto(dto: UttaksperiodeSpecDto) =
        UttaksperiodeSpec(
            Alder(dto.startAlder, dto.startMaaned),
            Uttaksgrad.from(dto.grad),
            dto.aarligInntekt
        )

    private companion object {

        /**
         * Temporary function for testing pensjonsavtaler with synthetic persons
         * (per June 2023 Norsk Pensjon does not support synthetic persons)
         */
        private fun mockAvtaler(spec: PensjonsavtaleSpecDto): Pensjonsavtaler {
            val uttaksperiode = if (spec.uttaksperioder.isEmpty()) UttaksperiodeSpecDto(
                67,
                1,
                100,
                10000
            ) else spec.uttaksperioder[0]
            val startAlder = uttaksperiode.startAlder
            val someNumber = System.currentTimeMillis().toString().substring(7).toInt()
            val startMaaned = someNumber % 12 + 1
            val sluttMaaned = (someNumber + startAlder) % 12 + 1

            return Pensjonsavtaler(
                listOf(
                    Pensjonsavtale(
                        "PENSJONSKAPITALBEVIS",
                        AvtaleKategori.INDIVIDUELL_ORDNING,
                        startAlder,
                        startAlder + 10,
                        listOf(
                            Utbetalingsperiode(
                                Alder(startAlder, startMaaned),
                                Alder(startAlder + 10, sluttMaaned),
                                someNumber,
                                Uttaksgrad.from(uttaksperiode.grad)
                            )
                        )
                    )
                ), emptyList()
            )
        }
    }
}
