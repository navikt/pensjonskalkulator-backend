package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningAfpResultDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningFolketrygdberegnetAfpDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningOpptjeningDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegningProblemDto
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import java.time.LocalDate

class ServiceberegningAfpResultMapperTest : ShouldSpec({

    context("success") {
        should("map all non-problem values") {
            ServiceberegningAfpResultMapper.fromDto(
                dto = ServiceberegningAfpResultDto(
                    beregnetAfp = ServiceberegningFolketrygdberegnetAfpDto(
                        afpTotalbeloep = 1,
                        virkningFom = LocalDate.of(2021, 2, 3),
                        tidligereArbeidsinntekt = 2,
                        grunnbeloep = 3,
                        sluttpoengtall = 4.1,
                        trygdetid = 5,
                        poengaar = 6,
                        poengaarFoer1992 = 7,
                        poengaarEtter1991 = 8,
                        grunnpensjon = 9,
                        tilleggspensjon = 10,
                        afpTillegg = 11,
                        fpp = 12.2,
                        saertillegg = 13,
                        grad = 14,
                        erAvkortet = true
                    ),
                    opptjeningListe = listOf(
                        ServiceberegningOpptjeningDto(
                            aarstall = 2000,
                            pensjonsgivendeInntekt = 555000,
                            pensjonspoeng = 4.6
                        )
                    ),
                    problem = null
                )
            ) shouldBe ServiceberegnetAfpResult(
                afpOrdning = null, // hard-coded
                beregnetAfp = BeregnetAfp(
                    totalbelopAfp = 1,
                    virkFom = LocalDate.of(2021, 2, 3),
                    tidligereArbeidsinntekt = 2,
                    grunnbelop = 3,
                    sluttpoengtall = 4.1,
                    trygdetid = 5,
                    poengar = 6,
                    poeangarF92 = 7,
                    poeangarE91 = 8,
                    grunnpensjon = 9,
                    tilleggspensjon = 10,
                    afpTillegg = 11,
                    fpp = 12.2,
                    sertillegg = 13,
                    grad = 14,
                    erAvkortet = true
                ),
                opptjeningListe = listOf(
                    AarligOpptjening(
                        aar = 2000,
                        pensjonsgivendeInntekt = 555000,
                        pensjonspoeng = 4.6,
                        omsorgspoeng = null,
                        maksimalUfoeregrad = null,
                        pensjonspoengType = "",
                        beholdning = 0,
                        merknadListe = emptyList()
                    )
                ),
                problem = null
            )
        }
    }

    context("problem") {
        should("map problem-describing values") {
            ServiceberegningAfpResultMapper.fromDto(
                dto = ServiceberegningAfpResultDto(
                    beregnetAfp = null,
                    opptjeningListe = emptyList(),
                    problem = ServiceberegningProblemDto(
                        type = "UTILSTREKKELIG_OPPTJENING",
                        beskrivelse = "feil"
                    )
                )
            ) shouldBe ServiceberegnetAfpResult(
                afpOrdning = null,
                beregnetAfp = null,
                opptjeningListe = emptyList(),
                problem = ServiceberegnetAfpProblem(
                    type = ServiceberegnetAfpProblemType.UTILSTREKKELIG_OPPTJENING,
                    beskrivelse = "feil"
                )
            )
        }
    }
})