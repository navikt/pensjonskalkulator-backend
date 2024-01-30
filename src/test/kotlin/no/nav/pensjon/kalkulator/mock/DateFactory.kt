package no.nav.pensjon.kalkulator.mock

import java.time.LocalDate
import java.util.*

object DateFactory {
    private const val TIME_ZONE_ID = "Europe/Oslo"
    private val locale = Locale.of("nb", "NO")
    private val timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)

    val date: LocalDate = LocalDate.of(2023, 4, 5)

    fun date(year: Int, month: Int): Date =
        Calendar.getInstance(timeZone, locale).also {
            it[Calendar.YEAR] = year
            it[Calendar.MONTH] = month
            it[Calendar.DAY_OF_MONTH] = 1
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
        }.time
}
