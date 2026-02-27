package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.MaanedligBeloep
import java.time.LocalDate

class LivsvarigOffentligAfpMapperV3Test : ShouldSpec({

    should("toDtoV3 mapping går gjennom alle feltene") {
        val source = AfpOffentligLivsvarigResult(
            afpInnvilget = true,
            virkningFom = LocalDate.of(2027, 9, 12),
            maanedligBeloepListe = listOf(
                MaanedligBeloep(fom = LocalDate.of(2020, 1, 2), beloep = 3),
                MaanedligBeloep(fom = LocalDate.of(2021, 4, 5), beloep = 6)
            ),
            sistBenyttetGrunnbeloep = 5
        )

        val dto = LivsvarigOffentligAfpMapperV3.toDtoV3(source)

        with(dto) {

            afpInnvilget shouldBe source.afpInnvilget
            virkningFom shouldBe source.virkningFom
            maanedligBeloepListe.size shouldBe source.maanedligBeloepListe.size
            maanedligBeloepListe[0].virkningFom shouldBe source.maanedligBeloepListe[0].fom
            maanedligBeloepListe[0].beloep shouldBe source.maanedligBeloepListe[0].beloep
            maanedligBeloepListe[1].virkningFom shouldBe source.maanedligBeloepListe[1].fom
            maanedligBeloepListe[1].beloep shouldBe source.maanedligBeloepListe[1].beloep
            sistBenyttetGrunnbeloep shouldBe source.sistBenyttetGrunnbeloep
        }
    }
})
