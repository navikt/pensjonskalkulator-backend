package no.nav.pensjon.kalkulator.ekskludering.api.map

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV2
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV2

object EkskluderingMapper {

    fun version1(source: EkskluderingStatus) =
        EkskluderingStatusV1(
            ekskludert = source.ekskludert,
            aarsak = EkskluderingAarsakV1.fromInternalValue(source.aarsak)
        )

    fun version2(source: EkskluderingStatus) =
        EkskluderingStatusV2(
            aarsak = EkskluderingAarsakV2.fromInternalValue(source.aarsak),
            ekskludert = source.ekskludert
        )
}
