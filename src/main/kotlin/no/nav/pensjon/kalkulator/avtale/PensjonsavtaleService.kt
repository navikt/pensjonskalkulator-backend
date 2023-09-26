package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleIngressSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UttaksperiodeIngressSpecDto
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
    fun fetchAvtaler(spec: PensjonsavtaleIngressSpecDto): Pensjonsavtaler {
        return if (featureToggleService.isEnabled("mock-norsk-pensjon"))
            mockAvtaler(spec)
        else
            filter(avtaleClient.fetchAvtaler(fromDto(spec)))
    }

    private fun fromDto(dto: PensjonsavtaleIngressSpecDto) =
        PensjonsavtaleSpec(
            pid = pidGetter.pid(),
            aarligInntektFoerUttak = dto.aarligInntektFoerUttak,
            uttaksperioder = dto.uttaksperioder.map(::fromUttaksperiodeSpecDto),
            antallInntektsaarEtterUttak = dto.antallInntektsaarEtterUttak,
            harAfp = dto.harAfp ?: false,
            harEpsPensjon = dto.harEpsPensjon,
            harEpsPensjonsgivendeInntektOver2G = dto.harEpsPensjonsgivendeInntektOver2G,
            antallAarIUtlandetEtter16 = dto.antallAarIUtlandetEtter16 ?: 0,
            sivilstatus = dto.sivilstatus,
            oenskesSimuleringAvFolketrygd = dto.oenskesSimuleringAvFolketrygd ?: false
        )

    private fun fromUttaksperiodeSpecDto(dto: UttaksperiodeIngressSpecDto) =
        UttaksperiodeSpec(
            start = Alder(dto.startAlder, dto.startMaaned - 1),
            grad = Uttaksgrad.from(dto.grad),
            aarligInntekt = dto.aarligInntekt
        )

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
        private fun mockAvtaler(spec: PensjonsavtaleIngressSpecDto): Pensjonsavtaler {
            val uttaksperiode = if (spec.uttaksperioder.isEmpty()) UttaksperiodeIngressSpecDto(
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
