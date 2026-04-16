package no.nav.pensjon.kalkulator.ansatt.enhet.client.navansatt.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet

class NavEnhetResultMapperTest : ShouldSpec({

    context("fromDto function") {
        should("map an empty DTO list to an empty TjenestekontorEnhet list") {
            NavEnhetResultMapper.fromDto(emptyList()).enhetListe shouldBe emptyList()
        }

        should("map a single-element DTO list to a single-element TjenestekontorEnhet list") {
            NavEnhetResultMapper.fromDto(
                listOf(NavEnhetResultDto(id = "1001", navn = "Kontor A", nivaa = "02"))
            ).enhetListe shouldBe listOf(TjenestekontorEnhet(id = "1001", navn = "Kontor A", nivaa = "02"))
        }

        should("map a multi-element DTO list to a multi-element TjenestekontorEnhet list") {
            NavEnhetResultMapper.fromDto(
                listOf(
                    NavEnhetResultDto(id = "1001", navn = "Kontor A", nivaa = "02"),
                    NavEnhetResultDto(id = "1002", navn = "Kontor B", nivaa = "03"),
                    NavEnhetResultDto(id = "1003", navn = "Kontor C", nivaa = "01")
                )
            ).enhetListe shouldBe listOf(
                TjenestekontorEnhet(id = "1001", navn = "Kontor A", nivaa = "02"),
                TjenestekontorEnhet(id = "1002", navn = "Kontor B", nivaa = "03"),
                TjenestekontorEnhet(id = "1003", navn = "Kontor C", nivaa = "01")
            )
        }
    }
})