package no.nav.pensjon.kalkulator.person

import mu.KotlinLogging
import java.time.DateTimeException
import java.time.LocalDate

/**
 * Person identifier, e.g. fødselsnummer (FNR).
 */
class Pid(argument: String) {

    val isValid = argument.length == FOEDSELSNUMMER_LENGTH
    val value = if (isValid) argument else "invalid"
    private val log = KotlinLogging.logger {}

    /**
     * Fødselsdatoen eller dagen og måneden fødselsnummeret ble utstedt.
     */
    val datoDel = if (isValid) datoDel(value) else value

    fun dato(): LocalDate {
        if (isValid.not()) {
            return LocalDate.of(1901, 1, 1)
        }

        val dayOfMonth = datoDel.substring(0, 2).toInt().let { if (it >= 40) it - 40 else it }
        val month = datoDel.substring(2, 4).toInt().let { if (it >= 80) it - 80 else if (it >= 40) it - 40 else it }
        val year = datoDel.substring(4, 6).toInt().let { if (it >= 20) 1900 + it else 2000 + it }

        return try {
            LocalDate.of(year, month, dayOfMonth)
        } catch (e: DateTimeException) {
            log.error(e) { "Unexpected date: $datoDel" }
            LocalDate.of(1902, 2, 2)
        }
    }

    val displayValue = if (isValid) redact(value) else value

    override fun toString(): String = displayValue

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean =
        (other as? Pid)?.let { value == it.value } == true

    companion object {
        private const val FOEDSELSNUMMER_LENGTH = 11
        private const val PERSONNUMMER_START_INDEX = 6

        fun redact(pid: String?): String =
            pid?.let(FoedselsnummerUtil::redact) ?: "<null>"

        /**
         * Fødselsdatoen eller dagen og måneden fødselsnummeret ble utstedt.
         */
        private fun datoDel(pid: String): String =
            pid.substring(0, PERSONNUMMER_START_INDEX)
    }
}
