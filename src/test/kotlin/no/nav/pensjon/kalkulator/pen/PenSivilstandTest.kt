package no.nav.pensjon.kalkulator.pen

import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PenSivilstandTest {

    @Test
    fun `from maps from sivilstand to PEN's equivalent sivilstand`() {
        assertEquals(PenSivilstand.GIFT, PenSivilstand.from(Sivilstand.GIFT))
        assertEquals(PenSivilstand.UGIF, PenSivilstand.from(Sivilstand.UGIFT))
        assertEquals(PenSivilstand.REPA, PenSivilstand.from(Sivilstand.REGISTRERT_PARTNER))
        assertEquals(PenSivilstand.ENKE, PenSivilstand.from(Sivilstand.ENKE_ELLER_ENKEMANN))
        assertEquals(PenSivilstand.NULL, PenSivilstand.from(Sivilstand.UOPPGITT))
    }
}
