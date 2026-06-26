package no.nav.pensjon.kalkulator.tech.time

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object NorwegianCalendar {
    val locale: Locale = Locale.of("nb", "NO")
    val timeZone: TimeZone = TimeZone.getTimeZone("Europe/Oslo")

    fun instance(): Calendar =
        Calendar.getInstance(timeZone, locale)

    fun forDate(date: Date) =
        instance().apply {
            time = date
            this[Calendar.HOUR_OF_DAY] = 0
            this[Calendar.MINUTE] = 0
            this[Calendar.SECOND] = 0
            this[Calendar.MILLISECOND] = 0
        }
}