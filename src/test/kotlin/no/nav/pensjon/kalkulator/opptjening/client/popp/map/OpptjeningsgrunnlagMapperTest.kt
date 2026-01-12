package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.InntektDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import java.math.BigDecimal

class OpptjeningsgrunnlagMapperTest : ShouldSpec({

    should("map response DTO to domain object") {
        val dto = OpptjeningsgrunnlagResponseDto(OpptjeningsgrunnlagDto(listOf(InntektDto("SUM_PI", 2023, 123L))))

        val grunnlag: Opptjeningsgrunnlag = OpptjeningsgrunnlagMapper.fromDto(dto)

        with(grunnlag.inntekter[0]) {
            type shouldBe Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT
            aar shouldBe 2023
            beloep shouldBe BigDecimal(123)
        }
    }
})
