package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
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
    fun `fromDto picks first adressebeskyttelsegradering`() {
        val dto = responseDto(adressebeskyttelser = listOf("FORTROLIG", "STRENGT_FORTROLIG"))
        assertEquals(AdressebeskyttelseGradering.FORTROLIG, PersonMapper.fromDto(dto)?.adressebeskyttelse)
    }

    @Test
    fun `fromDto maps DTO with null data to null`() {
        assertNull(PersonMapper.fromDto(PersonResponseDto(null, null, null)))
    }

    @Test
    fun `fromDto maps missing fornavn to empty string`() {
        assertEquals("", PersonMapper.fromDto(responseDto(fornavnliste = emptyList()))?.fornavn)
    }

    @Test
    fun `fromDto maps missing foedselsdato to minimum date`() {
        val person = PersonMapper.fromDto(responseDto(fodselsdatoer = emptyList()))!!
        assertEquals(LocalDate.MIN, person.foedselsdato)
        assertFalse(person.harFoedselsdato)
    }

    @Test
    fun `fromDto maps missing sivilstand to sivilstand null`() {
        assertEquals(Sivilstand.UOPPGITT, PersonMapper.fromDto(responseDto(sivilstander = emptyList()))?.sivilstand)
    }

    @Test
    fun `fromDto maps unknown sivilstand to sivilstand 'uoppgitt'`() {
        val dto = responseDto(sivilstander = listOf("not known"))
        assertEquals(Sivilstand.UNKNOWN, PersonMapper.fromDto(dto)?.sivilstand)
    }

    private companion object {
        val foedselsdato: LocalDate = LocalDate.of(1963, 12, 31)

        private fun responseDto(
            fornavnliste: List<String> = emptyList(),
            fodselsdatoer: List<LocalDate> = emptyList(),
            sivilstander: List<String> = emptyList(),
            adressebeskyttelser: List<String> = emptyList()
        ) =
            PersonResponseDto(
                data = PersonEnvelopeDto(
                    PersonDto(
                        fornavnliste.map(::NavnDto),
                        fodselsdatoer.map(::foedsel),
                        sivilstander.map(::SivilstandDto),
                        adressebeskyttelser.map(::AdressebeskyttelseDto)
                    )
                ),
                extensions = extensions(),
                errors = errors()
            )

        private fun foedsel(dato: LocalDate) = FoedselDto(DateDto(dato))

        private fun extensions() =
            ExtensionsDto(
                listOf(
                    WarningDto("query", "id", "code", "message", "details")
                )
            )

        private fun errors() =
            listOf(
                ErrorDto(message = "feil1"),
                ErrorDto(message = "feil2")
            )
    }
}
