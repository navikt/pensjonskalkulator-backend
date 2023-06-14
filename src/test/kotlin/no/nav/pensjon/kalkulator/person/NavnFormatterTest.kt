package no.nav.pensjon.kalkulator.person

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NavnFormatterTest {

    @Test
    fun `test fornavn`() {
        assertEquals("Cruella", NavnFormatter.formatNavn("CruElla", null, null))
        assertEquals("Cruella-D", NavnFormatter.formatNavn("CruElla-d", null, ""))
        assertEquals("Cruella-", NavnFormatter.formatNavn("CRuElla-", "", null))
        assertEquals("Cruella-De", NavnFormatter.formatNavn("CruElla-DE", "", " "))
        assertEquals("Cruella De", NavnFormatter.formatNavn("CruElla dE", " ", " "))
    }

    @Test
    fun `test mellomnavn`() {
        assertEquals("De", NavnFormatter.formatNavn("", "dE", null))
        assertEquals("Cruella", NavnFormatter.formatNavn(null, "CruEllA", ""))
    }

    @Test
    fun `test etternavn`() {
        assertEquals("Vil", NavnFormatter.formatNavn("", "", "ViL"))
        assertEquals("Cruella", NavnFormatter.formatNavn("", "", "CruEllA"))
    }

    @Test
    fun `test fullt navn`() {
        assertEquals("Cruella De Vil", NavnFormatter.formatNavn("Cruella", "De", "VIl"))
    }

    @Test
    fun `test for- og etternavn`() {
        assertEquals("Cruella Vil", NavnFormatter.formatNavn("CRUELLA", null, "ViL"))
    }

    @Test
    fun `test for- og mellomnavn`() {
        assertEquals("Cruella De", NavnFormatter.formatNavn("Cruella", "DE", ""))
    }

    @Test
    fun `formatName returns each navn part with capitalized first letter`() {
        assertEquals("Marve Almar Fleksnes", NavnFormatter.formatNavn("marve", "ALMAR", "FleksneS"))
    }

    @Test
    fun `formatName handles compund etternavn`() {
        assertEquals("Kari Hansen-Jensen", NavnFormatter.formatNavn("KARI", "", "hansen-JENSEN"))
    }

    @Test
    fun `formatName handles compund mellomnavn`() {
        assertEquals("Mellom Navn Hansen", NavnFormatter.formatNavn(null, "mellom NAVN", "hansen"))
    }

    @Test
    fun `formatName handles compund fornavn`() {
        assertEquals("Per Kari-Ola Mellom", NavnFormatter.formatNavn("per kari-OLA", "meLLom", null))
    }

    @Test
    fun `formatName handles nulls`() {
        assertEquals("", NavnFormatter.formatNavn(null, null, null))
    }
}
