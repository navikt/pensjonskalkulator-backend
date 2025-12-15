package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.LoependeInntekt
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import java.time.LocalDate

class TjenestepensjonSimuleringServiceTest : ShouldSpec({

    should("hente PID, mappe request, og hente simulering fra client") {
        val spec = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.of(1990, 1, 1),
            uttaksdato = LocalDate.of(2053, 3, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = listOf(
                LoependeInntekt(
                    fom = LocalDate.of(2053, 3, 1),
                    beloep = 500000
                ),
                LoependeInntekt(
                    fom = LocalDate.of(2060, 3, 1),
                    beloep = 0
                )
            ),
            aarIUtlandetEtter16 = 6,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
            erApoteker = false
        )
        val start = Alder(aar = 62, maaneder = 1)
        val slutt = Alder(aar = 63, maaneder = 1)

        val result = TjenestepensjonSimuleringService(
            pidGetter = mockk(relaxed = true),
            tjenestepensjonSimuleringClient = arrangeTjenestepensjon(start, slutt)
        ).hentTjenestepensjonSimulering(spec)

        with(result) {
            simuleringsResultatStatus.resultatType shouldBe ResultatType.OK
            tpOrdninger[0] shouldBe "tpOrdning"
            with(simuleringsResultat!!) {
                tpOrdning shouldBe "tpOrdning"
                tpNummer shouldBe "111111"
                betingetTjenestepensjonInkludert shouldBe true
                with(perioder[0]) {
                    startAlder shouldBe start
                    sluttAlder shouldBe slutt
                    maanedligBeloep shouldBe 1000
                }
            }
        }
    }
})

private fun arrangeTjenestepensjon(start: Alder, slutt: Alder): TjenestepensjonSimuleringClient =
    mockk<TjenestepensjonSimuleringClient>().apply {
        every {
            hentTjenestepensjonSimulering(any(), any())
        } returns OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.OK),
            simuleringsResultat = SimuleringsResultat(
                tpOrdning = "tpOrdning",
                tpNummer = "111111",
                perioder = listOf(Utbetaling(startAlder = start, sluttAlder = slutt, maanedligBeloep = 1000)),
                betingetTjenestepensjonInkludert = true
            ),
            tpOrdninger = listOf("tpOrdning")
        )
    }
