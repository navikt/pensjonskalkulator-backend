package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.*
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtalerV3
import org.junit.jupiter.api.Test

class NorskPensjonPensjonsavtaleMapperTest {

    @Test
    fun `fromDto maps DTO to avtale and selskap`() {
        NorskPensjonPensjonsavtaleMapper.fromDto(envelope()) shouldBe pensjonsavtalerV3()
    }

    private companion object {
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
                kategori = "individuellOrdning"
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
                aarsakManglendeGradering = AarsakManglendeGradering.IKKE_STOTTET
                aarsakIkkeBeregnet = AarsakIkkeBeregnet.UKJENT_PRODUKTTYPE
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
                sluttMaaned = 2
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
    }
}
