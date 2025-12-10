package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.LivsvarigOffentligAfpResultV2

object LivsvarigOffentligAfpMapperV2 {

    fun toDtoV2(source: AfpOffentligLivsvarigResult) =
        LivsvarigOffentligAfpResultV2(
            afpStatus = source.afpStatus,
            virkningFom = source.virkningFom,
            maanedligBeloep = source.maanedligBeloep,
            sistBenyttetGrunnbeloep = source.sistBenyttetGrunnbeloep
        )
}
