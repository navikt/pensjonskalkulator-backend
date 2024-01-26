package no.nav.pensjon.kalkulator.ekskludering.api.dto

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EkskluderingAarsakV1Test {

    @Test
    fun `'fromInternalValue' mapper intern ekskludering-aarsak til API-ets ekskludering-aarsak`() {
        assertEquals(EkskluderingAarsakV1.NONE, EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.NONE))
        assertEquals(
            EkskluderingAarsakV1.HAR_GJENLEVENDEYTELSE,
            EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.HAR_GJENLEVENDEYTELSE)
        )
        assertEquals(
            EkskluderingAarsakV1.HAR_LOEPENDE_UFOERETRYGD,
            EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.HAR_LOEPENDE_UFOERETRYGD)
        )
        assertEquals(
            EkskluderingAarsakV1.ER_APOTEKER,
            EkskluderingAarsakV1.fromInternalValue(EkskluderingAarsak.ER_APOTEKER)
        )
    }
}
