package no.nav.pensjon.kalkulator.person

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class NavnFormatterTest : ShouldSpec({

    should("formattere fornavn") {
        NavnFormatter.formatNavn("CruElla", null, null) shouldBe "Cruella"
        NavnFormatter.formatNavn("CruElla-d", null, "") shouldBe "Cruella-D"
        NavnFormatter.formatNavn("CRuElla-", "", null) shouldBe "Cruella-"
        NavnFormatter.formatNavn("CruElla-DE", "", " ") shouldBe "Cruella-De"
        NavnFormatter.formatNavn("CruElla dE", " ", " ") shouldBe "Cruella De"
    }

    should("formattere mellomnavn") {
        NavnFormatter.formatNavn("", "dE", null) shouldBe "De"
        NavnFormatter.formatNavn(null, "CruEllA", "") shouldBe "Cruella"
    }

    should("formattere etternavn") {
        NavnFormatter.formatNavn("", "", "ViL") shouldBe "Vil"
        NavnFormatter.formatNavn("", "", "CruEllA") shouldBe "Cruella"
    }

    should("formattere fullt navn") {
        NavnFormatter.formatNavn("Cruella", "De", "VIl") shouldBe "Cruella De Vil"
    }

    should("formattere for- og etternavn") {
        NavnFormatter.formatNavn("CRUELLA", null, "ViL") shouldBe "Cruella Vil"
    }

    should("formattere for- og mellomnavn") {
        NavnFormatter.formatNavn("Cruella", "DE", "") shouldBe "Cruella De"
    }

    should("return each navn part with capitalized first letter") {
        NavnFormatter.formatNavn("marve", "ALMAR", "FleksneS") shouldBe "Marve Almar Fleksnes"
    }

    should("handle compund etternavn") {
        NavnFormatter.formatNavn("KARI", "", "hansen-JENSEN") shouldBe "Kari Hansen-Jensen"
    }

    should("handle compund mellomnavn") {
        NavnFormatter.formatNavn(null, "mellom NAVN", "hansen") shouldBe "Mellom Navn Hansen"
    }

    should("handle compund fornavn") {
        NavnFormatter.formatNavn("per kari-OLA", "meLLom", null) shouldBe "Per Kari-Ola Mellom"
    }

    should("handle nulls") {
        NavnFormatter.formatNavn(null, null, null) shouldBe ""
    }
})
