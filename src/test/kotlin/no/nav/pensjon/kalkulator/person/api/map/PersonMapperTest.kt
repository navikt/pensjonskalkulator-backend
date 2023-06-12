package no.nav.pensjon.kalkulator.person.api.map

import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PersonMapperTest {

    @Test
    fun toDto() {
        val dto = PersonMapper.toDto(person())

        assertEquals("Fornavn1", dto.fornavn)
        assertEquals(Sivilstand.UOPPGITT, dto.sivilstand)
    }
}
