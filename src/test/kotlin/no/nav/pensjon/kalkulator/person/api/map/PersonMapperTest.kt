package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.person.api.dto.ApiSivilstand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PersonMapperTest {

    @Test
    fun `toDto maps domain person to API person DTO`() {
        val dto = PersonMapper.toDto(person())

        assertEquals("Fornavn1", dto.fornavn)
        assertEquals(LocalDate.of(1963, 12, 31), dto.foedselsdato)
        assertEquals(ApiSivilstand.UOPPGITT, dto.sivilstand)
    }
}
