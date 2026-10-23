package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema
import no.nav.pensjon.kalkulator.simulering.EpsSpec

/**
 * Informasjon om ektefelle/partner/samboer (EPS).
 */
data class SimuleringV2EpsSpec(
    @field:Schema(description = "Informasjon om nålevende ektefelle/partner/samboer")
    val levende: SimuleringV2LevendeEpsSpec? = null,

    @field:Schema(description = "Informasjon om avdød ektefelle/partner/samboer")
    val avdoed: SimuleringV2AvdoedEpsSpec? = null
) {
    fun toInternalValue() =
        EpsSpec(
            levende = levende?.toInternalValue(),
            avdoed = avdoed?.toInternalValue()
        )
}