package no.nav.pensjon.kalkulator.opptjening

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class OpptjeningstypeTest {

    @Test
    fun `forCode returns 'Sum pensjonsgivende inntekt' when code is 'SUM_PI'`() {
        assertEquals(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, Opptjeningstype.forCode("SUM_PI"))
    }

    @Test
    fun `forCode returns 'Other' when code is unknown`() {
        assertEquals(Opptjeningstype.OTHER, Opptjeningstype.forCode("unknown"))
    }
}
