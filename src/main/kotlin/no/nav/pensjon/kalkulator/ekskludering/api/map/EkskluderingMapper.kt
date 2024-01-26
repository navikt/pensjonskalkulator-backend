package no.nav.pensjon.kalkulator.ekskludering.api.map

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV1

object EkskluderingMapper {

    fun version1(source: EkskluderingStatus) =
        EkskluderingStatusV1(
            ekskludert = source.ekskludert,
            aarsak = EkskluderingAarsakV1.fromInternalValue(source.aarsak)
        )
}
