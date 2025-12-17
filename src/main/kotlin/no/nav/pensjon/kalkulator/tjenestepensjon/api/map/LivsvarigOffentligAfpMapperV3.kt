package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v3.LivsvarigOffentligAfpResultV3
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v3.MaanedligBeloepV3

object LivsvarigOffentligAfpMapperV3 {

    fun toDtoV3(source: AfpOffentligLivsvarigResult) =
        LivsvarigOffentligAfpResultV3(
            afpStatus = source.afpStatus,
            virkningFom = source.virkningFom,
            maanedligBeloepListe = source.maanedligBeloepListe.map { MaanedligBeloepV3(it.fom, it.beloep) },
            sistBenyttetGrunnbeloep = source.sistBenyttetGrunnbeloep
        )
}
