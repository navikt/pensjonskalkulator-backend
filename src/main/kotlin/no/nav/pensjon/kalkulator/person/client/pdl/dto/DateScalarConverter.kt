package no.nav.pensjon.kalkulator.person.client.pdl.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateScalarConverter {
    fun toJson(value: LocalDate): String = value.format(DateTimeFormatter.ISO_LOCAL_DATE)
    fun toScalar(rawValue: String): LocalDate = LocalDate.parse(rawValue, DateTimeFormatter.ISO_LOCAL_DATE)
}

