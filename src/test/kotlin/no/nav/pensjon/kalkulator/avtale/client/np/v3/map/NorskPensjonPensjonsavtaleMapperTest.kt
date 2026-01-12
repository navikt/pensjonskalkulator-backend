package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.InntektSpec
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtalerV3
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid

class NorskPensjonPensjonsavtaleMapperTest : ShouldSpec({

    context("fromDto") {
        should("map data transfer object to domain object comprising avtale and selskap") {
            NorskPensjonPensjonsavtaleMapper.fromDto(envelope()) shouldBe pensjonsavtalerV3()
        }
    }

    context("toDto") {
        should("map domain object to data transfer object") {
            NorskPensjonPensjonsavtaleMapper.toDto(
                PensjonsavtaleSpec(
                    aarligInntektFoerUttak = 1,
                    uttaksperioder = emptyList()
                ), pid
            ) shouldBe
                    NorskPensjonPensjonsavtaleSpecDto(
                        pid = pid,
                        aarligInntektFoerUttak = 1,
                        uttaksperioder = emptyList(),
                        antallInntektsaarEtterUttak = 0 // since no uttaksperioder
                    )
        }

        /**
         * NB: Norsk Pensjon's documentation says that 14 represents "livsvarig".
         * However, using 14 makes Norsk Pensjon return the error "No signature in message!".
         * As a workoround 13 is used instead (although this represents "13 years" instead of "livsvarig").
         */
        should("limit antallInntektsaarEtterUttak to 'livsvarig value' 13") {
            val antallInntektsaarEtterUttak = 20 // more than max. value of 13
            val startAlderAar = 62
            val domainObject = PensjonsavtaleSpec(
                aarligInntektFoerUttak = 20_000,
                uttaksperioder = listOf(
                    UttaksperiodeSpec(
                        startAlder = Alder(startAlderAar, 0),
                        grad = Uttaksgrad.FEMTI_PROSENT,
                        aarligInntekt = InntektSpec(
                            aarligBeloep = 10_000,
                            tomAlder = Alder(aar = 66, maaneder = 11)
                        )
                    ),
                    UttaksperiodeSpec(
                        startAlder = Alder(67, 0),
                        grad = Uttaksgrad.HUNDRE_PROSENT,
                        aarligInntekt = InntektSpec(
                            aarligBeloep = 5_000,
                            tomAlder = Alder(aar = startAlderAar + antallInntektsaarEtterUttak, maaneder = 0)
                        )
                    ),
                )
            )

            NorskPensjonPensjonsavtaleMapper.toDto(
                spec = domainObject,
                pid
            ).antallInntektsaarEtterUttak shouldBe 13 // max. is 13 (which represents 'livsvarig')
        }

        should("give antall inntektsår etter uttak = 0 when årlig inntekt is null") {
            val domainObject = PensjonsavtaleSpec(
                aarligInntektFoerUttak = 20_000,
                uttaksperioder = listOf(
                    UttaksperiodeSpec(
                        startAlder = Alder(aar = 67, maaneder = 0),
                        grad = Uttaksgrad.HUNDRE_PROSENT,
                        aarligInntekt = null
                    )
                )
            )

            NorskPensjonPensjonsavtaleMapper.toDto(
                spec = domainObject,
                pid
            ).antallInntektsaarEtterUttak shouldBe 0
        }
    }
})

private fun envelope() = EnvelopeDto().apply { body = body() }

private fun body() = BodyDto().apply { pensjonsrettigheter = pensjonsrettigheter() }

private fun pensjonsrettigheter() =
    PensjonsrettigheterDto().apply {
        pensjonsRettigheter = listOf(pensjonsrettighet())
        utilgjengeligeInnretninger = listOf(utilgjengeligInnretning())
    }

private fun pensjonsrettighet() =
    PensjonsrettighetDto().apply {
        avtalenummer = "Avtale1"
        arbeidsgiver = "Firma1"
        selskapsnavn = "Selskap1"
        produktbetegnelse = "Produkt1"
        kategori = "individuelleOrdninger"
        underkategori = "Foreningskollektiv"
        innskuddssaldo = 1000
        naavaerendeAvtaltAarligInnskudd = 100
        pensjonsbeholdningForventet = 1000000
        pensjonsbeholdningNedreGrense = 900000
        pensjonsbeholdningOvreGrense = 1100000
        avkastningsgaranti = null
        beregningsmodell = "bransjeavtale"
        startAlder = 70
        sluttAlder = 80
        aarsakManglendeGradering = "IKKE_STOTTET"
        aarsakIkkeBeregnet = "UKJENT_PRODUKTTYPE"
        opplysningsdato = "2023-01-01"
        utbetalingsperioder = listOf(
            utbetalingsperiodeMedSluttalder(),
            utbetalingsperiodeUtenSluttalder()
        )
    }

private fun utbetalingsperiodeMedSluttalder() =
    UtbetalingsperiodeDto().apply {
        startAlder = 71
        startMaaned = 1
        sluttAlder = 81
        sluttMaaned = 3
        aarligUtbetalingForventet = 10000
        aarligUtbetalingNedreGrense = 0
        aarligUtbetalingOvreGrense = 0
        grad = 100
    }

private fun utbetalingsperiodeUtenSluttalder() =
    UtbetalingsperiodeDto().apply {
        startAlder = 72
        startMaaned = 2
        aarligUtbetalingForventet = 20000
        aarligUtbetalingNedreGrense = 0
        aarligUtbetalingOvreGrense = 0
        grad = 80
    }

private fun utilgjengeligInnretning() =
    UtilgjengeligInnretningDto().apply {
        selskapsnavn = "Selskap1"
        heltUtilgjengelig = true
        antallManglendeRettigheter = 1
        kategori = "folketrygd"
        feilkode = "Feil1"
    }
