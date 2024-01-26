package no.nav.pensjon.kalkulator.ekskludering

import no.nav.pensjon.kalkulator.sak.SakType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class EkskluderingAarsakTest {

    @Test
    fun `'from' mapper sak-type til ekskludering-aarsak`() {
        assertEquals(EkskluderingAarsak.NONE, EkskluderingAarsak.from(SakType.NONE))
        assertEquals(EkskluderingAarsak.HAR_GJENLEVENDEYTELSE, EkskluderingAarsak.from(SakType.GJENLEVENDEYTELSE))
        assertEquals(EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD, EkskluderingAarsak.from(SakType.UFOERETRYGD))
        assertEquals(EkskluderingAarsak.NONE, EkskluderingAarsak.from(SakType.GENERELL))
    }
}
