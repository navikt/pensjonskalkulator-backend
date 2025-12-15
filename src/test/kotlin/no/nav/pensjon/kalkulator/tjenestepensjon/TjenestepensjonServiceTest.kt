package no.nav.pensjon.kalkulator.tjenestepensjon

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import java.time.LocalDate

class TjenestepensjonServiceTest : ShouldSpec({

    context("erApoteker") {
        should("returnere 'true' når ekstern tjeneste gir 'true'") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { erApoteker(any()) } returns true
            }

            TjenestepensjonService(
                tjenestepensjonClient = client,
                pidGetter = arrangePid(),
                featureToggleService = mockk(relaxed = true)
            ).erApoteker() shouldBe true
        }
    }

    context("harTjenestepensjonsforhold") {
        should("returnere 'true' når ekstern tjeneste gir tjenestepensjonsforhold") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { tjenestepensjon(any()) } returns Tjenestepensjon(forholdList = listOf(forhold()))
            }

            TjenestepensjonService(
                tjenestepensjonClient = client,
                pidGetter = arrangePid(),
                featureToggleService = mockk(relaxed = true)
            ).harTjenestepensjonsforhold() shouldBe true
        }
    }

    context("hentMedlemskapITjenestepensjonsordninger") {
        should("returnere liste over alle medlemskap i tjenestepensjonsordninger") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { tjenestepensjonsforhold(any()) } returns tjenestepensjonMedMedlemskap()
            }

            TjenestepensjonService(
                tjenestepensjonClient = client,
                pidGetter = arrangePid(),
                featureToggleService = mockk(relaxed = true)
            ).hentMedlemskapITjenestepensjonsordninger() shouldBe
                    listOf("Maritim pensjonskasse", "Statens pensjonskasse", "Kommunal Landspensjonskasse")
        }
    }

    context("hentAfpOffentligLivsvarigDetaljer") {
        should("returnere korrekte detaljer når personen har en tjenestepensjonsordning") {
            val tpNr = "3010"
            val expectedResult = AfpOffentligLivsvarigResult(
                afpStatus = true,
                virkningFom = LocalDate.of(2025, 1, 1),
                maanedligBeloep = 15000,
                sistBenyttetGrunnbeloep = 123000
            )
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(any()) } returns listOf(tpNr)
                every { hentAfpOffentligLivsvarigDetaljer(any(), any(), any()) } returns expectedResult
            }

            val expectedUttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)

            TjenestepensjonService(
                tjenestepensjonClient = client,
                pidGetter = arrangePid(),
                featureToggleService = mockk(relaxed = true)
            ).hentAfpOffentligLivsvarigDetaljer() shouldBe expectedResult

            verify { client.afpOffentligLivsvarigTpNummerListe(pid) }
            verify { client.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, expectedUttaksdato) }
        }

        should("kaste exception når personen ikke har noen tjenestepensjonsordninger") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(any()) } returns emptyList()
            }

            TjenestepensjonService(
                tjenestepensjonClient = client,
                pidGetter = arrangePid(),
                featureToggleService = mockk(relaxed = true)
            ).hentAfpOffentligLivsvarigDetaljer() shouldBe
                    AfpOffentligLivsvarigResult(
                        afpStatus = null,
                        virkningFom = null,
                        maanedligBeloep = null,
                        sistBenyttetGrunnbeloep = null
                    )

            verify { client.afpOffentligLivsvarigTpNummerListe(pid) }
            confirmVerified(client)
        }

        should("kaste exception når personen har flere tjenestepensjonsordninger") {
            val tpNumre = listOf("3010", "3020", "3030")
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(any()) } returns tpNumre
            }

            val exception = shouldThrow<EgressException> {
                TjenestepensjonService(
                    tjenestepensjonClient = client,
                    pidGetter = arrangePid(),
                    featureToggleService = mockk(relaxed = true)
                ).hentAfpOffentligLivsvarigDetaljer()
            }

            with(exception.message!!) {
                contains("Bruker har flere ordninger for livsvarig offentlig AFP") shouldBe true
                contains("(3)") shouldBe true
            }
            verify { client.afpOffentligLivsvarigTpNummerListe(pid) }
            confirmVerified(client)
        }

        should("bruke neste måned som uttaksdato") {
            val tpNr = "3010"
            val expectedResult = AfpOffentligLivsvarigResult(
                afpStatus = false,
                virkningFom = null,
                maanedligBeloep = null,
                sistBenyttetGrunnbeloep = null
            )
            val expectedUttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(any()) } returns listOf(tpNr)
                every { hentAfpOffentligLivsvarigDetaljer(any(), any(), any()) } returns expectedResult
            }

            TjenestepensjonService(
                tjenestepensjonClient = client,
                pidGetter = arrangePid(),
                featureToggleService = mockk(relaxed = true)
            ).hentAfpOffentligLivsvarigDetaljer()

            verify { client.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, expectedUttaksdato) }
        }
    }
})

private fun arrangePid(): PidGetter =
    mockk<PidGetter>().apply {
        every { pid() } returns pid
    }

private fun tjenestepensjonMedMedlemskap() =
    Tjenestepensjonsforhold(
        tpOrdninger = listOf(
            "Maritim pensjonskasse",
            "Statens pensjonskasse",
            "Kommunal Landspensjonskasse"
        )
    )

private fun forhold(tpOrdning: String = "") =
    Forhold(
        ordning = tpOrdning,
        ytelser = emptyList(),
        datoSistOpptjening = null
    )
