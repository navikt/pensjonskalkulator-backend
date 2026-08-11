package no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.afp.BeregnetAfp
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.BeregnetAfpDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpProblemDto
import no.nav.pensjon.kalkulator.afp.client.pensjonssimulator.dto.ServiceberegnetAfpResultDto
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import java.time.LocalDate

class ServiceberegnetAfpResultMapperTest : ShouldSpec({

    context("success") {
        should("map all non-problem values") {
            ServiceberegnetAfpResultMapper.fromDto(
                dto = ServiceberegnetAfpResultDto(
                    afpOrdning = "AFPKOM",
                    beregnetAfp = BeregnetAfpDto(
                        totalbelopAfp = 1,
                        virkFom = LocalDate.of(2021, 2, 3),
                        tidligereArbeidsinntekt = 2,
                        grunnbelop = 3,
                        sluttpoengtall = 4.1,
                        trygdetid = 5,
                        poengar = 6,
                        poeangar_f92 = 7,
                        poeangar_e91 = 8,
                        grunnpensjon = 9,
                        tilleggspensjon = 10,
                        afpTillegg = 11,
                        fpp = 12.2,
                        sertillegg = 13,
                        grad = 14,
                        erAvkortet = true
                    ),
                    problem = null
                )
            ) shouldBe ServiceberegnetAfpResult(
                afpOrdning = AfpOrdningType.AFPKOM,
                beregnetAfp = BeregnetAfp(
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
                problem = null
            )
        }
    }

    context("problem") {
        should("map problem-describing values") {
            ServiceberegnetAfpResultMapper.fromDto(
                dto = ServiceberegnetAfpResultDto(
                    afpOrdning = null,
                    beregnetAfp = null,
                    problem = ServiceberegnetAfpProblemDto(
                        type = "UTILSTREKKELIG_OPPTJENING",
                        beskrivelse = "feil"
                    )
                )
            ) shouldBe ServiceberegnetAfpResult(
                afpOrdning = null,
                beregnetAfp = null,
                problem = ServiceberegnetAfpProblem(
                    type = ServiceberegnetAfpProblemType.UTILSTREKKELIG_OPPTJENING,
                    beskrivelse = "feil"
                )
            )
        }
    }
})