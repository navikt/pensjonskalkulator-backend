package no.nav.pensjon.kalkulator.tech.time

import no.nav.pensjon.kalkulator.mock.DateFactory
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.util.*

class DateUtilTest {

    @Test
    fun `toDate converts LocalDate in summer to Date`() {
        assertEquals(date(Calendar.JULY), DateUtil.toDate(LocalDate.of(YEAR, 7, 1)))
    }

    @Test
    fun `toDate converts LocalDate in winter to Date`() {
        assertEquals(date(Calendar.FEBRUARY), DateUtil.toDate(LocalDate.of(YEAR, 2, 1)))
    }

    companion object {
        private const val YEAR = 2023

        private fun date(month: Int): Date = DateFactory.date(YEAR, month)
    }
}
