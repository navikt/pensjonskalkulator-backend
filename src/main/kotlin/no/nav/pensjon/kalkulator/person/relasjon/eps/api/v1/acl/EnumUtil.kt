package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import no.nav.pensjon.kalkulator.common.api.EnumUtil.handleMissingExternalValue

object EnumUtil {

    fun missingExternalValue(type: String, value: Any?): Nothing {
        handleMissingExternalValue(apiId = "person.relasjon.eps.api.v1", type, value)
    }
}