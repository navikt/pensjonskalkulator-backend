package no.nav.pensjon.kalkulator.ansatt.enhet

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.ansatt.enhet.client.EnhetClient
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType

class EnhetServiceTest : ShouldSpec({

    val ansattIdGetter = mockk<SecurityContextNavIdExtractor>().apply {
        every { id() } returns ANSATT_ID
    }

    val client: EnhetClient = mockk()
    val service = EnhetService(client, ansattIdGetter)

    fun verifyCalls() {
        verify { client.fetchTjenestekontorEnhetListe(ansattId = ANSATT_ID) }
        verify { ansattIdGetter.id() }
    }

    context("success - client returns data") {
        should("return a list of TjenestekontorEnhet") {
            val expectedEnhetList = listOf(
                TjenestekontorEnhet("enhet1", "Tjenestekontor 1", nivaa = "02"),
                TjenestekontorEnhet("enhet2", "Tjenestekontor 2", nivaa = "03"),
            )
            every { client.fetchTjenestekontorEnhetListe(ansattId = ANSATT_ID) } returns TjenestekontorEnheter(enhetListe = expectedEnhetList)

            service.tjenestekontorEnhetListe().enhetListe shouldBe expectedEnhetList

            verifyCalls()
        }
    }

    context("failure - client returns no data") {
        should("return an empty list and no problem description") {
            val expectedResult = TjenestekontorEnheter(
                enhetListe = emptyList(),
                problem = null
            )
            every { client.fetchTjenestekontorEnhetListe(ansattId = ANSATT_ID) } returns expectedResult

            service.tjenestekontorEnhetListe() shouldBe expectedResult

            verifyCalls()
        }
    }

    context("failure - ansatt not found") {
        should("return an empty list and a problem description") {
            val expectedResult = TjenestekontorEnheter(
                enhetListe = emptyList(),
                problem = Problem(
                    type = ProblemType.PERSON_IKKE_FUNNET,
                    beskrivelse = "User with ID $ANSATT_ID not found"
                )
            )
            every { client.fetchTjenestekontorEnhetListe(ansattId = ANSATT_ID) } returns expectedResult

            service.tjenestekontorEnhetListe() shouldBe expectedResult

            verifyCalls()
        }
    }
})

private const val ANSATT_ID = "X123456"