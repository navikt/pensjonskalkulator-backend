package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.MaanedligBeloep
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v4.LivsvarigOffentligAfpResultV4
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.v4.MaanedligBeloepV4
import java.time.LocalDate

class LivsvarigOffentligAfpMapperV4Test : ShouldSpec({

    should("map all fields to V4") {
        val fom = LocalDate.of(2025, 10, 1)
        val nesteFom = LocalDate.of(2026, 1, 1)
        val source = AfpOffentligLivsvarigResult(
            afpInnvilget = true,
            virkningFom = LocalDate.of(2025, 9, 1),
            maanedligBeloepListe = listOf(MaanedligBeloep(fom, 15000), MaanedligBeloep(nesteFom, 16000)),
            sistBenyttetGrunnbeloep = 123000,
            tpNummer = "3010",
            tpOrdningNavn = "Statens pensjonskasse"
        )

        LivsvarigOffentligAfpMapperV4.toDtoV4(source) shouldBe LivsvarigOffentligAfpResultV4(
            afpInnvilget = true,
            virkningFom = LocalDate.of(2025, 9, 1),
            maanedligBeloepListe = listOf(MaanedligBeloepV4(fom, 15000), MaanedligBeloepV4(nesteFom, 16000)),
            sistBenyttetGrunnbeloep = 123000,
            tpNummer = "3010",
            tpOrdningNavn = "Statens pensjonskasse"
        )
    }

    should("preserve null fields when no AFP scheme exists") {
        LivsvarigOffentligAfpMapperV4.toDtoV4(
            AfpOffentligLivsvarigResult(null, null, emptyList(), null)
        ) shouldBe LivsvarigOffentligAfpResultV4(null, emptyList(), null, null, null, null)
    }

    should("preserve scheme details when AFP is not granted") {
        val source = AfpOffentligLivsvarigResult(
            afpInnvilget = false,
            virkningFom = null,
            maanedligBeloepListe = emptyList(),
            sistBenyttetGrunnbeloep = null,
            tpNummer = "3010",
            tpOrdningNavn = "Statens pensjonskasse"
        )

        LivsvarigOffentligAfpMapperV4.toDtoV4(source) shouldBe LivsvarigOffentligAfpResultV4(
            false, emptyList(), null, null, "3010", "Statens pensjonskasse"
        )
    }

    should("preserve scheme details when AFP status is unknown") {
        val source = AfpOffentligLivsvarigResult(
            afpInnvilget = null,
            virkningFom = null,
            maanedligBeloepListe = emptyList(),
            sistBenyttetGrunnbeloep = null,
            tpNummer = "3020",
            tpOrdningNavn = "Kommunal Landspensjonskasse"
        )

        LivsvarigOffentligAfpMapperV4.toDtoV4(source) shouldBe LivsvarigOffentligAfpResultV4(
            afpInnvilget = null,
            maanedligBeloepListe = emptyList(),
            virkningFom = null,
            sistBenyttetGrunnbeloep = null,
            tpNummer = "3020",
            tpOrdningNavn = "Kommunal Landspensjonskasse"
        )
    }
})
