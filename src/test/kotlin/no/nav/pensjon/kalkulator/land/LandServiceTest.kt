package no.nav.pensjon.kalkulator.land

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class LandServiceTest : ShouldSpec({
    val landListe = LandService().landListe()

    should("inkludere Antarktis") {
        landListe.filter { it.landkode == Land.ATA.name } shouldHaveSize 1
    }

    should("ekskludere historiske land") {
        landListe.filter { it.landkode == Land.SUN.name } shouldBe emptyList()
    }

    should("ekskludere Norge") {
        landListe.filter { it.landkode == Land.NOR.name } shouldBe emptyList()
    }
})
