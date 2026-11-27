package no.nav.pensjon.kalkulator.lagring

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.lagring.SanityVisningsvilkaar.*

class ForbeholdSeksjonTest : ShouldSpec({

    fun seksjon(vararg grupper: List<SanityVisningsvilkaar>) =
        ForbeholdSeksjon(tittel = null, avsnitt = emptyList(), vilkaarGrupper = grupper.toList())

    should("vise seksjon uten regelgrupper alltid") {
        seksjon().erSynligMed(emptyList()) shouldBe true
    }

    should("kreve alle vilkår innenfor en gruppe (OG)") {
        val seksjon = seksjon(listOf(BEREGNER_AFP_GENERELT, HAR_UFOERETRYGD))

        seksjon.erSynligMed(listOf(BEREGNER_AFP_GENERELT)) shouldBe false
        seksjon.erSynligMed(listOf(BEREGNER_AFP_GENERELT, HAR_UFOERETRYGD)) shouldBe true
    }

    should("vise seksjon når én av gruppene er oppfylt (ELLER)") {
        val seksjon = seksjon(listOf(HAR_UTENLANDSOPPHOLD), listOf(HAR_VEDTAK_UTENLANDSOPPHOLD))

        seksjon.erSynligMed(listOf(HAR_UTENLANDSOPPHOLD)) shouldBe true
        seksjon.erSynligMed(listOf(HAR_VEDTAK_UTENLANDSOPPHOLD)) shouldBe true
        seksjon.erSynligMed(listOf(HAR_UFOERETRYGD)) shouldBe false
        seksjon.erSynligMed(emptyList()) shouldBe false
    }
})
