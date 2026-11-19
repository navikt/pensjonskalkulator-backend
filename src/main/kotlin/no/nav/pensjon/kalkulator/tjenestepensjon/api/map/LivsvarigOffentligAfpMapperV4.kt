package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v4.LivsvarigOffentligAfpResultV4
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v4.MaanedligBeloepV4

object LivsvarigOffentligAfpMapperV4 {

    fun toDtoV4(source: AfpOffentligLivsvarigResult) =
        LivsvarigOffentligAfpResultV4(
            afpInnvilget = source.afpInnvilget,
            virkningFom = source.virkningFom,
            maanedligBeloepListe = source.maanedligBeloepListe.map { MaanedligBeloepV4(it.fom, it.beloep) },
            sistBenyttetGrunnbeloep = source.sistBenyttetGrunnbeloep,
            tpNummer = source.tpNummer,
            tpOrdningNavn = source.tpOrdningNavn
        )
}
