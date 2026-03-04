package no.nav.pensjon.kalkulator.common.api

object EnumUtil {

    fun handleMissingExternalValue(apiId: String, type: String, value: Any?): Nothing {
        throw IllegalArgumentException("Ingen ekstern verdi i $apiId for $type $value")
    }
}