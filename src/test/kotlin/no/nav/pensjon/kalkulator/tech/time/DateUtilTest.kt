package no.nav.pensjon.kalkulator.tech.time

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.DateFactory
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class DateUtilTest : ShouldSpec({

    should("convert LocalDate in summer to Date") {
        DateUtil.toDate(LocalDate.of(YEAR, 7, 1)) shouldBe date(Calendar.JULY)
    }

    should("convert LocalDate in winter to Date") {
        DateUtil.toDate(LocalDate.of(YEAR, 2, 1)) shouldBe date(Calendar.FEBRUARY)
    }

    should("convert UTC date-time to local date") {
        val utcDateTime = ZonedDateTime.of(YEAR, 1, 31, 23, 0, 0, 0, ZoneId.of("UTC"))
        DateUtil.toLocalDate(utcDateTime) shouldBe LocalDate.of(YEAR, 2, 1)
    }
})

private const val YEAR = 2023

private fun date(month: Int): Date =
    DateFactory.date(YEAR, month)
