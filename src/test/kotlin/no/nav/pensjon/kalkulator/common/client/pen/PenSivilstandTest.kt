package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PenSivilstandTest {

    @Test
    fun `fromInternalValue maps from sivilstand to PEN's equivalent sivilstand`() {
        assertEquals(PenSivilstand.GIFT, PenSivilstand.fromInternalValue(Sivilstand.GIFT))
        assertEquals(PenSivilstand.UGIFT, PenSivilstand.fromInternalValue(Sivilstand.UGIFT))
        assertEquals(PenSivilstand.REGISTRERT_PARTNER, PenSivilstand.fromInternalValue(Sivilstand.REGISTRERT_PARTNER))
        assertEquals(PenSivilstand.ENKE_ELLER_ENKEMANN, PenSivilstand.fromInternalValue(Sivilstand.ENKE_ELLER_ENKEMANN))
        assertEquals(PenSivilstand.UDEFINERT, PenSivilstand.fromInternalValue(Sivilstand.UOPPGITT))
    }
}
