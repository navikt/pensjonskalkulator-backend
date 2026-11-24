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

    context("harAfpOffentligLivsvarig") {
        listOf(
            emptyList<String>() to false,
            listOf("3010") to true,
            listOf("3010", "3020") to true
        ).forEach { (tpNumre, expected) ->
            should("return $expected for ${tpNumre.size} AFP schemes without fetching details or scheme names") {
                val client = mockk<TjenestepensjonClient>().apply {
                    every { afpOffentligLivsvarigTpNummerListe(pid) } returns tpNumre
                }

                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .harAfpOffentligLivsvarig() shouldBe expected

                verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
                confirmVerified(client)
            }
        }

        should("propagate errors when fetching the AFP scheme list") {
            val exception = EgressException("Kunne ikke hente AFP-ordninger")
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } throws exception
            }

            shouldThrow<EgressException> {
                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .harAfpOffentligLivsvarig()
            } shouldBe exception

            verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
            confirmVerified(client)
        }
    }

    context("hentAfpOffentligLivsvarigDetaljerMedTpOrdning") {
        should("include the scheme number and registry name") {
            val result = AfpOffentligLivsvarigResult(
                afpInnvilget = true,
                virkningFom = LocalDate.of(2025, 1, 1),
                maanedligBeloepListe = listOf(
                    MaanedligBeloep(LocalDate.of(2025, 1, 1), 15000),
                    MaanedligBeloep(LocalDate.of(2026, 1, 1), 16000)
                ),
                sistBenyttetGrunnbeloep = 123000
            )
            val expectedUttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } returns listOf("3010")
                every { hentAfpOffentligLivsvarigDetaljer(pid, "3010", expectedUttaksdato) } returns result
                every { hentTpOrdningNavn("3010") } returns "Statens pensjonskasse"
            }

            TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                .hentAfpOffentligLivsvarigDetaljerMedTpOrdning() shouldBe result.copy(
                    tpNummer = "3010",
                    tpOrdningNavn = "Statens pensjonskasse"
                )

            result.tpNummer shouldBe null
            result.tpOrdningNavn shouldBe null
            verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
            verify(exactly = 1) { client.hentAfpOffentligLivsvarigDetaljer(pid, "3010", expectedUttaksdato) }
            verify(exactly = 1) { client.hentTpOrdningNavn("3010") }
            confirmVerified(client)
        }

        should("skip the registry lookup when no scheme exists") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } returns emptyList()
            }

            TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                .hentAfpOffentligLivsvarigDetaljerMedTpOrdning() shouldBe
                    AfpOffentligLivsvarigResult(null, null, emptyList(), null)

            verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
            confirmVerified(client)
        }

        should("reject multiple schemes without fetching details or scheme names") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } returns listOf("3010", "3020")
            }

            shouldThrow<EgressException> {
                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .hentAfpOffentligLivsvarigDetaljerMedTpOrdning()
            }.message shouldBe "Bruker har flere ordninger for livsvarig offentlig AFP (2). Dette er ikke støttet."

            verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
            confirmVerified(client)
        }

        listOf(false, null).forEach { afpInnvilget ->
            should("include scheme details when afpInnvilget is $afpInnvilget") {
                val result = AfpOffentligLivsvarigResult(
                    afpInnvilget = afpInnvilget,
                    virkningFom = null,
                    maanedligBeloepListe = emptyList(),
                    sistBenyttetGrunnbeloep = null
                )
                val client = mockk<TjenestepensjonClient>().apply {
                    every { afpOffentligLivsvarigTpNummerListe(pid) } returns listOf("3020")
                    every { hentAfpOffentligLivsvarigDetaljer(pid, "3020", any()) } returns result
                    every { hentTpOrdningNavn("3020") } returns "Kommunal Landspensjonskasse"
                }

                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .hentAfpOffentligLivsvarigDetaljerMedTpOrdning() shouldBe result.copy(
                        tpNummer = "3020",
                        tpOrdningNavn = "Kommunal Landspensjonskasse"
                    )

                verify(exactly = 1) { client.hentTpOrdningNavn("3020") }
            }
        }

        should("propagate errors when fetching the scheme list") {
            val exception = EgressException("Kunne ikke hente AFP-ordninger")
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } throws exception
            }

            shouldThrow<EgressException> {
                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .hentAfpOffentligLivsvarigDetaljerMedTpOrdning()
            } shouldBe exception

            verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
            confirmVerified(client)
        }

        should("propagate AFP errors without fetching the scheme name") {
            val exception = EgressException("Kunne ikke hente AFP-detaljer")
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } returns listOf("3010")
                every { hentAfpOffentligLivsvarigDetaljer(pid, "3010", any()) } throws exception
            }

            shouldThrow<EgressException> {
                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .hentAfpOffentligLivsvarigDetaljerMedTpOrdning()
            } shouldBe exception

            verify(exactly = 1) { client.afpOffentligLivsvarigTpNummerListe(pid) }
            verify(exactly = 1) { client.hentAfpOffentligLivsvarigDetaljer(pid, "3010", any()) }
            confirmVerified(client)
        }

        should("propagate registry errors") {
            val client = mockk<TjenestepensjonClient>().apply {
                every { afpOffentligLivsvarigTpNummerListe(pid) } returns listOf("3010")
                every { hentAfpOffentligLivsvarigDetaljer(pid, "3010", any()) } returns
                        AfpOffentligLivsvarigResult(false, null, emptyList(), null)
                every { hentTpOrdningNavn("3010") } throws EgressException("Registry unavailable")
            }

            shouldThrow<EgressException> {
                TjenestepensjonService(client, arrangePid(), mockk(relaxed = true))
                    .hentAfpOffentligLivsvarigDetaljerMedTpOrdning()
            }.message shouldBe "Registry unavailable"
        }
    }

    context("hentAfpOffentligLivsvarigDetaljer") {
        should("returnere korrekte detaljer når personen har en tjenestepensjonsordning") {
            val tpNr = "3010"
            val fom = LocalDate.of(2025, 1, 1)
            val expectedResult = AfpOffentligLivsvarigResult(
                afpInnvilget = true,
                virkningFom = fom,
                maanedligBeloepListe = listOf(MaanedligBeloep(fom, 15000),
                    MaanedligBeloep(fom.plusMonths(1), 16000)),
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
            verify(exactly = 0) { client.hentTpOrdningNavn(any()) }
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
                        afpInnvilget = null,
                        virkningFom = null,
                        maanedligBeloepListe = emptyList(),
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
                afpInnvilget = false,
                virkningFom = null,
                maanedligBeloepListe = emptyList(),
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
