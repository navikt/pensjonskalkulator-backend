package no.nav.pensjon.kalkulator.person

import java.time.LocalDate

/**
 * Person identifier, e.g. fødselsnummer (FNR).
 */
class Pid(argument: String) {

    val isValid = argument.length == FNR_LENGTH
    val value = if (isValid) argument else "invalid"

    /**
     * Fødselsdatoen eller dagen og måneden fødselsnummeret ble utstedt.
     */
    val datoDel = if (isValid) value.substring(0, PERSONNUMMER_START_INDEX) else value

    fun dato(): LocalDate {
        if (isValid.not()) {
            return LocalDate.of(1900, 1, 1)
        }

        val dayOfMonth = datoDel.substring(0, 2).toInt()
        val month = datoDel.substring(2, 4).toInt().let { if (it >= 80) it - 80 else it }
        val year = datoDel.substring(4, 6).toInt().let { if (it >= 20) 1900 + it else 2000 + it }
        return LocalDate.of(year, month, dayOfMonth)
    }

    val displayValue = if (isValid) "$datoDel*****" else value

    override fun toString(): String {
        return displayValue
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? Pid)?.let { value == it.value } ?: false
    }

    companion object {
        private const val FNR_LENGTH = 11
        private const val PERSONNUMMER_START_INDEX = 6
    }
}
