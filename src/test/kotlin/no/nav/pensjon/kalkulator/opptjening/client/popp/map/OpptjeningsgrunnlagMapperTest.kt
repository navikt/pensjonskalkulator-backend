package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.InntektDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import java.math.BigDecimal

class OpptjeningsgrunnlagMapperTest : ShouldSpec({

    should("map data transfer object to domain object") {
        val dto = OpptjeningsgrunnlagResponseDto(
            opptjeningsGrunnlag = OpptjeningsgrunnlagDto(
                inntektListe = listOf(
                    InntektDto(
                        inntektType = "SUM_PI",
                        inntektAr = 2023,
                        belop = 123L
                    )
                )
            )
        )

        OpptjeningsgrunnlagMapper.fromDto(dto) shouldBe Opptjeningsgrunnlag(
            inntekter = listOf(
                Inntekt(
                    type = Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT,
                    aar = 2023,
                    beloep = BigDecimal(123)
                )
            )
        )
    }
})
