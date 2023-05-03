package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.Land
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PersonMapperTest {

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val dto = responseDto("UGIFT")
        assertEquals(Sivilstand.UGIFT, PersonMapper.fromDto(dto).sivilstand)
    }

    @Test
    fun `fromDto picks first sivilstand`() {
        val dto = responseDto(listOf(SivilstandDto("UGIFT"), SivilstandDto("SKILT")))
        assertEquals(Sivilstand.UGIFT, PersonMapper.fromDto(dto).sivilstand)
    }

    @Test
    fun `fromDto maps missing sivilstand to sivilstand 'uoppgitt'`() {
        assertEquals(Sivilstand.UOPPGITT, PersonMapper.fromDto(PersonResponseDto(null, null)).sivilstand)
        assertEquals(Sivilstand.UOPPGITT, PersonMapper.fromDto(responseDto(emptyList())).sivilstand)
    }

    @Test
    fun `fromDto maps unknown sivilstand to sivilstand 'uoppgitt'`() {
        val dto = responseDto("not known")
        assertEquals(Sivilstand.UOPPGITT, PersonMapper.fromDto(dto).sivilstand)
    }

    private fun responseDto(sivilstand: String) = responseDto(listOf(SivilstandDto(sivilstand)))

    private fun responseDto(sivilstander: List<SivilstandDto>) =
        PersonResponseDto(
            PersonEnvelopeDto(
                PersonDto(
                    listOf(FoedselDto(LocalDate.MIN)),
                    listOf(StatsborgerskapDto(Land.OTHER.code)),
                    sivilstander
                )
            ), null
        )
}
