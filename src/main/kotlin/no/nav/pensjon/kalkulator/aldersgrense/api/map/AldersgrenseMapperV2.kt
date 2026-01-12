package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseSpec as AldersgrenseSpecV1V2
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV2
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlderV2
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.AldersgrenseSpec

object AldersgrenseMapperV2 {

    fun fromDto(source: AldersgrenseSpecV1V2) =
        AldersgrenseSpec(aarskull = source.foedselsdato)

    fun dto(source: Aldersgrenser) =
        AldersgrenseResultV2(
            normertPensjoneringsalder = PersonAlderV2(source.normalder.aar, source.normalder.maaneder),
            nedreAldersgrense = PersonAlderV2(source.nedreAlder.aar, source.nedreAlder.maaneder),
            oevreAldersgrense = PersonAlderV2(source.oevreAlder.aar, source.oevreAlder.maaneder),
        )
}
