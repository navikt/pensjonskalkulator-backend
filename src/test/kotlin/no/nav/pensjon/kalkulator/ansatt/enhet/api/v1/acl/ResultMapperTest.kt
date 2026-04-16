package no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnheter
import no.nav.pensjon.kalkulator.ansatt.enhet.TjenestekontorEnhet
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType

class ResultMapperTest : ShouldSpec({

    context("success - single element") {
        should("map to a single-element AnsattEnhetV1Tjenestekontor list") {
            ResultMapper.toDto(
                source = TjenestekontorEnheter(
                    enhetListe = listOf(TjenestekontorEnhet(id = "1001", navn = "Kontor A", nivaa = "02"))
                )
            ) shouldBe AnsattEnhetV1Result(
                enhetListe = listOf(AnsattEnhetV1Tjenestekontor(id = "1001", navn = "Kontor A")),
                problem = null
            )
        }
    }

    context("success - multiple elements") {
        should("map  to a multi-element AnsattEnhetV1Tjenestekontor list") {
            ResultMapper.toDto(
                source = TjenestekontorEnheter(
                    enhetListe =
                        listOf(
                            TjenestekontorEnhet(id = "1001", navn = "Kontor A", nivaa = "02"),
                            TjenestekontorEnhet(id = "1002", navn = "Kontor B", nivaa = "03"),
                            TjenestekontorEnhet(id = "1003", navn = "Kontor C", nivaa = "01")
                        )
                )
            ) shouldBe AnsattEnhetV1Result(
                enhetListe = listOf(
                    AnsattEnhetV1Tjenestekontor(id = "1001", navn = "Kontor A"),
                    AnsattEnhetV1Tjenestekontor(id = "1002", navn = "Kontor B"),
                    AnsattEnhetV1Tjenestekontor(id = "1003", navn = "Kontor C")
                ),
                problem = null
            )
        }
    }

    context("failure - empty list") {
        should("map an empty TjenestekontorEnhet list to an empty AnsattEnhetV1Tjenestekontor list") {
            ResultMapper.toDto(source = TjenestekontorEnheter(enhetListe = emptyList())) shouldBe
                    AnsattEnhetV1Result(enhetListe = emptyList(), problem = null)
        }
    }

    context("failure - ansatt not found") {
        should("map the problem to a problem DTO") {
            ResultMapper.toDto(
                source = TjenestekontorEnheter(
                    enhetListe = emptyList(),
                    problem = Problem(
                        type = ProblemType.PERSON_IKKE_FUNNET,
                        beskrivelse = "User with ID X123456 not found"
                    )
                )
            ) shouldBe
                    AnsattEnhetV1Result(
                        enhetListe = emptyList(), problem = AnsattEnhetV1Problem(
                            kode = AnsattEnhetV1ProblemType.ANSATT_IKKE_FUNNET,
                            beskrivelse = "User with ID X123456 not found"
                        )
                    )
        }
    }
})