package no.nav.pensjon.kalkulator.ekskludering.api.map

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.ekskludering.api.dto.ApotekerStatusV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV2
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV2

/**
 * Anti-corruption layer.
 * Maps from internal domain to data transfer objects.
 */
object EkskluderingMapper {

    fun statusV1(source: EkskluderingStatus) =
        EkskluderingStatusV1(
            ekskludert = source.ekskludert,
            aarsak = EkskluderingAarsakV1.fromInternalValue(value = source.aarsak)
        )

    fun statusV2(source: EkskluderingStatus) =
        EkskluderingStatusV2(
            aarsak = EkskluderingAarsakV2.fromInternalValue(value = source.aarsak),
            ekskludert = source.ekskludert
        )

    fun apotekerStatusV1(source: EkskluderingStatus) =
        ApotekerStatusV1(
            aarsak = EkskluderingAarsakV2.fromInternalValue(value = source.aarsak),
            apoteker = source.ekskludert
        )
}
