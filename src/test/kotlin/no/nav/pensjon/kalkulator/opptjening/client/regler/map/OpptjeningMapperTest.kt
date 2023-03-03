package no.nav.pensjon.kalkulator.opptjening.client.regler.map

import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningSpec
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningshistorikkSpec
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningRequestDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningResponseDto
import no.nav.pensjon.kalkulator.opptjening.client.regler.dto.OpptjeningsdetaljerDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class OpptjeningMapperTest {

    @Test
    fun `toDto maps request spec to DTO`() {
        val dto = OpptjeningMapper.toDto(opptjeningshistorikkSpec())

        val foedselsdato = foedselsdato(dto)
        assertEquals(1963, foedselsdato.get(Calendar.YEAR))
        assertEquals(Calendar.JANUARY, foedselsdato.get(Calendar.MONTH))
        assertEquals(1, foedselsdato.get(Calendar.DAY_OF_MONTH))
        assertEquals("PPI", dto.personOpptjeningsgrunnlagListe[0].opptjening.opptjeningType.kode)
    }

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val opptjeningshistorikk = OpptjeningMapper.fromDto(
            OpptjeningResponseDto(ArrayList(listOf(opptjeningDto())))
        )

        val opptjening = opptjeningshistorikk.opptjeningPerAar[2022]!!
        assertEquals(123456, opptjening.anvendtPensjonsgivendeInntekt)
        assertEquals(BigDecimal("1.23"), opptjening.pensjonspoeng)
    }

    companion object {

        private fun opptjeningDto() =
            OpptjeningDto(
                OpptjeningsdetaljerDto(2022, 123456, BigDecimal("1.23"))
            )

        private fun opptjeningshistorikkSpec() =
            OpptjeningshistorikkSpec(
                listOf(OpptjeningSpec(2022, 1000000, Opptjeningstype.PENSJONSGIVENDE_INNTEKT)), foedselsdato()
            )

        private fun foedselsdato() = LocalDate.of(1963, 1, 1)

        private fun foedselsdato(dto: OpptjeningRequestDto): Calendar {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))
            calendar.time = dto.personOpptjeningsgrunnlagListe[0].fodselsdato
            return calendar
        }
    }
}
