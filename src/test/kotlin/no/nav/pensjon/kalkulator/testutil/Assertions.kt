package no.nav.pensjon.kalkulator.testutil

import no.nav.pensjon.kalkulator.general.Alder
import org.junit.jupiter.api.Assertions.assertEquals

object Assertions {

    fun assertAlder(expectedAar: Int, expectedMaaneder: Int, actualAlder: Alder) {
        with(actualAlder) {
            assertEquals(expectedAar, aar)
            assertEquals(expectedMaaneder, maaneder)
        }
    }
}
