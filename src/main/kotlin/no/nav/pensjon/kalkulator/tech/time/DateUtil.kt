package no.nav.pensjon.kalkulator.tech.time

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

object DateUtil {
    const val MAANEDER_PER_AAR = 12
    private const val TIME_ZONE_ID = "Europe/Oslo"
    private val locale = Locale.of("nb", "NO")
    private val timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)
    private val zoneId = ZoneId.of(TIME_ZONE_ID)

    fun toEpoch(date: LocalDate): Long =
        date.atStartOfDay(zoneId).toInstant().toEpochMilli()

    fun toLocalDate(time: ZonedDateTime): LocalDate =
        time.withZoneSameInstant(ZoneId.of(TIME_ZONE_ID)).toLocalDate()

    fun toDate(localDate: LocalDate): Date =
        Calendar.getInstance(timeZone, locale).also {
            it[Calendar.YEAR] = localDate.year
            it[Calendar.MONTH] = localDate.monthValue - 1
            it[Calendar.DAY_OF_MONTH] = localDate.dayOfMonth
            it[Calendar.HOUR_OF_DAY] = 0
            it[Calendar.MINUTE] = 0
            it[Calendar.SECOND] = 0
            it[Calendar.MILLISECOND] = 0
        }.time
}
