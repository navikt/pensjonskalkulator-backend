package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class PdlPersonMapperTest {

    @Test
    fun `fromDto maps response DTO to domain object`() {
        val dto = responseDto(
            fornavnListe = listOf("For-Navn"),
            foedselsdatoListe = listOf(foedselsdato),
            sivilstandListe = listOf("UGIFT")
        )

        val person = PdlPersonMapper.fromDto(dto)

        assertEquals("For-Navn", person.navn)
        assertEquals(foedselsdato, person.foedselsdato)
        assertTrue(person.harFoedselsdato)
        assertEquals(Sivilstand.UGIFT, person.sivilstand)
    }

    @Test
    fun `fromDto picks first fornavn`() {
        val dto = responseDto(fornavnListe = listOf("Kari", "Ola"))
        assertEquals("Kari", PdlPersonMapper.fromDto(dto).navn)
    }

    @Test
    fun `fromDto picks first foedselsdato`() {
        val dto = responseDto(foedselsdatoListe = listOf(foedselsdato, LocalDate.of(1962, 1, 1)))
        assertEquals(foedselsdato, PdlPersonMapper.fromDto(dto).foedselsdato)
    }

    @Test
    fun `fromDto picks first sivilstand`() {
        val dto = responseDto(sivilstandListe = listOf("UGIFT", "SKILT"))
        assertEquals(Sivilstand.UGIFT, PdlPersonMapper.fromDto(dto).sivilstand)
    }

    @Test
    fun `fromDto picks first adressebeskyttelsegradering`() {
        val dto = responseDto(adressebeskyttelseListe = listOf("FORTROLIG", "STRENGT_FORTROLIG"))
        assertEquals(AdressebeskyttelseGradering.FORTROLIG, PdlPersonMapper.fromDto(dto).adressebeskyttelse)
    }

    @Test
    fun `fromDto maps DTO with null data to null`() {
        val exception = assertThrows<NotFoundException> {
            PdlPersonMapper.fromDto(PdlPersonResult(data = null, extensions = null, errors = null))
        }

        assertEquals("person", exception.message)
    }

    @Test
    fun `fromDto maps missing fornavn to empty string`() {
        assertEquals("", PdlPersonMapper.fromDto(responseDto(fornavnListe = emptyList())).navn)
    }

    @Test
    fun `fromDto maps missing foedselsdato to minimum date`() {
        val person = PdlPersonMapper.fromDto(responseDto(foedselsdatoListe = emptyList()))
        assertEquals(LocalDate.MIN, person.foedselsdato)
        assertFalse(person.harFoedselsdato)
    }

    @Test
    fun `fromDto maps missing sivilstand to sivilstand null`() {
        assertEquals(Sivilstand.UOPPGITT, PdlPersonMapper.fromDto(responseDto(sivilstandListe = emptyList())).sivilstand)
    }

    @Test
    fun `fromDto maps unknown sivilstand to sivilstand 'uoppgitt'`() {
        val dto = responseDto(sivilstandListe = listOf("not known"))
        assertEquals(Sivilstand.UNKNOWN, PdlPersonMapper.fromDto(dto).sivilstand)
    }

    private companion object {
        val foedselsdato: LocalDate = LocalDate.of(1963, 12, 31)

        private fun responseDto(
            fornavnListe: List<String> = emptyList(),
            foedselsdatoListe: List<LocalDate> = emptyList(),
            sivilstandListe: List<String> = emptyList(),
            adressebeskyttelseListe: List<String> = emptyList()
        ) =
            PdlPersonResult(
                data = PdlPersonEnvelope(
                    PdlPerson(
                        navn = fornavnListe.map(::PdlNavn),
                        foedselsdato = foedselsdatoListe.map(::foedsel),
                        sivilstand = sivilstandListe.map(::PdlSivilstand),
                        adressebeskyttelse = adressebeskyttelseListe.map(::PdlAdressebeskyttelse)
                    )
                ),
                extensions = extensions(),
                errors = errors()
            )

        private fun foedsel(dato: LocalDate) = PdlFoedselsdato(PdlDate(dato))

        private fun extensions() =
            PdlExtensions(
                warnings = listOf(
                    PdlWarning(
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
                PdlError(message = "feil1"),
                PdlError(message = "feil2")
            )
    }
}
