package no.nav.pensjon.kalkulator.opptjening.client.popp.map

import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.InntektDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagDto
import no.nav.pensjon.kalkulator.opptjening.client.popp.dto.OpptjeningsgrunnlagResponseDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OpptjeningsgrunnlagMapperTest {

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val dto = OpptjeningsgrunnlagResponseDto(OpptjeningsgrunnlagDto(listOf(InntektDto("SUM_PI", 2023, 123L))))

        val grunnlag = OpptjeningsgrunnlagMapper.fromDto(dto)

        val inntekt = grunnlag.inntekter[0]
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, inntekt.type)
        assertEquals(2023, inntekt.aar)
        assertEquals(BigDecimal(123), inntekt.beloep)
    }
}
