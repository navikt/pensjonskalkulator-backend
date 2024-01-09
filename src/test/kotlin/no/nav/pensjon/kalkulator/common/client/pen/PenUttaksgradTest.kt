package no.nav.pensjon.kalkulator.common.client.pen

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class PenUttaksgradTest {

    @Test
    fun `fromInternalValue maps from uttaksgrad to PEN's equivalent uttaksgrad`() {
        assertEquals(PenUttaksgrad.NULL, PenUttaksgrad.fromInternalValue(Uttaksgrad.NULL))
        assertEquals(PenUttaksgrad.TJUE_PROSENT, PenUttaksgrad.fromInternalValue(Uttaksgrad.TJUE_PROSENT))
        assertEquals(PenUttaksgrad.FOERTI_PROSENT, PenUttaksgrad.fromInternalValue(Uttaksgrad.FOERTI_PROSENT))
        assertEquals(PenUttaksgrad.FEMTI_PROSENT, PenUttaksgrad.fromInternalValue(Uttaksgrad.FEMTI_PROSENT))
        assertEquals(PenUttaksgrad.SEKSTI_PROSENT, PenUttaksgrad.fromInternalValue(Uttaksgrad.SEKSTI_PROSENT))
        assertEquals(PenUttaksgrad.AATTI_PROSENT, PenUttaksgrad.fromInternalValue(Uttaksgrad.AATTI_PROSENT))
        assertEquals(PenUttaksgrad.HUNDRE_PROSENT, PenUttaksgrad.fromInternalValue(Uttaksgrad.HUNDRE_PROSENT))
    }
}
