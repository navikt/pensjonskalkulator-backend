package no.nav.pensjon.kalkulator.tech.time

import java.time.LocalDate

fun interface DateProvider {
    fun now(): LocalDate
}
