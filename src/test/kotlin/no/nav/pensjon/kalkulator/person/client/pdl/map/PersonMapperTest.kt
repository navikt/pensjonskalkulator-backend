package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class PersonMapperTest {

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val dto = responseDto(
            fornavnListe = listOf("For-Navn"),
            foedselsdatoListe = listOf(foedselsdato),
            sivilstandListe = listOf("UGIFT")
        )

        val person = PersonMapper.fromDto(dto)

        assertEquals("For-Navn", person.navn)
        assertEquals(foedselsdato, person.foedselsdato)
        assertTrue(person.harFoedselsdato)
        assertEquals(Sivilstand.UGIFT, person.sivilstand)
    }

    @Test
    fun `fromDto picks first fornavn`() {
        val dto = responseDto(fornavnListe = listOf("Kari", "Ola"))
        assertEquals("Kari", PersonMapper.fromDto(dto).navn)
    }

    @Test
    fun `fromDto picks first foedselsdato`() {
        val dto = responseDto(foedselsdatoListe = listOf(foedselsdato, LocalDate.of(1962, 1, 1)))
        assertEquals(foedselsdato, PersonMapper.fromDto(dto).foedselsdato)
    }

    @Test
    fun `fromDto picks first sivilstand`() {
        val dto = responseDto(sivilstandListe = listOf("UGIFT", "SKILT"))
        assertEquals(Sivilstand.UGIFT, PersonMapper.fromDto(dto).sivilstand)
    }

    @Test
    fun `fromDto picks first adressebeskyttelsegradering`() {
        val dto = responseDto(adressebeskyttelseListe = listOf("FORTROLIG", "STRENGT_FORTROLIG"))
        assertEquals(AdressebeskyttelseGradering.FORTROLIG, PersonMapper.fromDto(dto).adressebeskyttelse)
    }

    @Test
    fun `fromDto maps DTO with null data to null`() {
        val exception = assertThrows<NotFoundException> {
            PersonMapper.fromDto(PersonResponseDto(data = null, extensions = null, errors = null))
        }

        assertEquals("person", exception.message)
    }

    @Test
    fun `fromDto maps missing fornavn to empty string`() {
        assertEquals("", PersonMapper.fromDto(responseDto(fornavnListe = emptyList())).navn)
    }

    @Test
    fun `fromDto maps missing foedselsdato to minimum date`() {
        val person = PersonMapper.fromDto(responseDto(foedselsdatoListe = emptyList()))
        assertEquals(LocalDate.MIN, person.foedselsdato)
        assertFalse(person.harFoedselsdato)
    }

    @Test
    fun `fromDto maps missing sivilstand to sivilstand null`() {
        assertEquals(Sivilstand.UOPPGITT, PersonMapper.fromDto(responseDto(sivilstandListe = emptyList())).sivilstand)
    }

    @Test
    fun `fromDto maps unknown sivilstand to sivilstand 'uoppgitt'`() {
        val dto = responseDto(sivilstandListe = listOf("not known"))
        assertEquals(Sivilstand.UNKNOWN, PersonMapper.fromDto(dto).sivilstand)
    }

    private companion object {
        val foedselsdato: LocalDate = LocalDate.of(1963, 12, 31)

        private fun responseDto(
            fornavnListe: List<String> = emptyList(),
            foedselsdatoListe: List<LocalDate> = emptyList(),
            sivilstandListe: List<String> = emptyList(),
            adressebeskyttelseListe: List<String> = emptyList()
        ) =
            PersonResponseDto(
                data = PersonEnvelopeDto(
                    PersonDto(
                        navn = fornavnListe.map(::NavnDto),
                        foedsel = foedselsdatoListe.map(::foedsel),
                        sivilstand = sivilstandListe.map(::SivilstandDto),
                        adressebeskyttelse = adressebeskyttelseListe.map(::AdressebeskyttelseDto)
                    )
                ),
                extensions = extensions(),
                errors = errors()
            )

        private fun foedsel(dato: LocalDate) = FoedselDto(DateDto(dato))

        private fun extensions() =
            ExtensionsDto(
                warnings = listOf(
                    WarningDto(
                        query = "query",
                        id = "id",
                        code = "code",
                        message = "message",
                        details = "details"
                    )
                )
            )

        private fun errors() =
            listOf(
                ErrorDto(message = "feil1"),
                ErrorDto(message = "feil2")
            )
    }
}
