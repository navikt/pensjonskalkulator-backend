package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

object EnumUtil {

    fun missingExternalValue(type: String, value: Any?): Nothing {
        throw IllegalArgumentException("Ingen ekstern verdi i person.relasjon.eps.api.v1 for $type $value")
    }
}