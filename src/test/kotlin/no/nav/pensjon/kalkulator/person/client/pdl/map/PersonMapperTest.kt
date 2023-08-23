package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class PersonMapperTest {

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val dto = responseDto(
            fornavnliste = listOf("For-Navn"),
            fodselsdatoer = listOf(foedselsdato),
            sivilstander = listOf("UGIFT")
        )
        val person = PersonMapper.fromDto(dto)!!

        assertEquals("For-Navn", person.fornavn)
        assertEquals(foedselsdato, person.foedselsdato)
        assertTrue(person.harFoedselsdato)
        assertEquals(Sivilstand.UGIFT, person.sivilstand)
    }

    @Test
    fun `fromDto picks first fornavn`() {
        val dto = responseDto(fornavnliste = listOf("Kari", "Ola"))
        assertEquals("Kari", PersonMapper.fromDto(dto)?.fornavn)
    }

    @Test
    fun `fromDto picks first foedselsdato`() {
        val dto = responseDto(fodselsdatoer = listOf(foedselsdato, LocalDate.of(1962, 1, 1)))
        assertEquals(foedselsdato, PersonMapper.fromDto(dto)?.foedselsdato)
    }

    @Test
    fun `fromDto picks first sivilstand`() {
        val dto = responseDto(sivilstander = listOf("UGIFT", "SKILT"))
        assertEquals(Sivilstand.UGIFT, PersonMapper.fromDto(dto)?.sivilstand)
    }

    @Test
    fun `fromDto maps DTO with null data to null`() {
        assertNull(PersonMapper.fromDto(PersonResponseDto(null, null)))
    }

    @Test
    fun `fromDto maps missing fornavn to fornavn null`() {
        assertNull(PersonMapper.fromDto(responseDto(fornavnliste = emptyList()))?.fornavn)
    }

    @Test
    fun `fromDto maps missing foedselsdato to minimum date`() {
        val person = PersonMapper.fromDto(responseDto(fodselsdatoer = emptyList()))!!
        assertEquals(LocalDate.MIN, person.foedselsdato)
        assertFalse(person.harFoedselsdato)
    }

    @Test
    fun `fromDto maps missing sivilstand to sivilstand null`() {
        assertNull(PersonMapper.fromDto(responseDto(sivilstander = emptyList()))?.sivilstand)
    }

    @Test
    fun `fromDto maps unknown sivilstand to sivilstand 'uoppgitt'`() {
        val dto = responseDto(sivilstander = listOf("not known"))
        assertEquals(Sivilstand.UOPPGITT, PersonMapper.fromDto(dto)?.sivilstand)
    }

    private companion object {
        val foedselsdato = LocalDate.of(1963, 12, 31)

        private fun responseDto(
            fornavnliste: List<String> = emptyList(),
            fodselsdatoer: List<LocalDate> = emptyList(),
            sivilstander: List<String> = emptyList()
        ) =
            PersonResponseDto(
                PersonEnvelopeDto(
                    PersonDto(
                        fornavnliste.map(::NavnDto),
                        fodselsdatoer.map { FoedselDto(DateDto(it)) },
                        sivilstander.map(::SivilstandDto)
                    )
                ), null
            )
    }
}
