package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PenSivilstandTest {

    @Test
    fun `from maps from sivilstand to PEN's equivalent sivilstand`() {
        assertEquals(PenSivilstand.GIFT, PenSivilstand.from(Sivilstand.GIFT))
        assertEquals(PenSivilstand.UGIFT, PenSivilstand.from(Sivilstand.UGIFT))
        assertEquals(PenSivilstand.REGISTRERT_PARTNER, PenSivilstand.from(Sivilstand.REGISTRERT_PARTNER))
        assertEquals(PenSivilstand.ENKE_ELLER_ENKEMANN, PenSivilstand.from(Sivilstand.ENKE_ELLER_ENKEMANN))
        assertEquals(PenSivilstand.UDEFINERT, PenSivilstand.from(Sivilstand.UOPPGITT))
    }
}
