package no.nav.pensjon.kalkulator.aldersgrense.api.map

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseResultV2
import no.nav.pensjon.kalkulator.aldersgrense.api.dto.PersonAlderV2
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser

object AldersgrenseMapperV2 {
    fun dto(source: Aldersgrenser): AldersgrenseResultV2 =
        AldersgrenseResultV2(
            normertPensjoneringsalder = PersonAlderV2(source.normalder.aar, source.normalder.maaneder),
            nedreAldersgrense = PersonAlderV2(source.nedreAlder.aar, source.nedreAlder.maaneder),
            oevreAldersgrense = PersonAlderV2(source.oevreAlder.aar, source.oevreAlder.maaneder),
        )
}
