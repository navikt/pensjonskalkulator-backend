package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.LoependeInntekt
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringSpecMapperV3
import java.time.LocalDate

class TjenestepensjonSimuleringSpecMapperV3Test : ShouldSpec({

    val spec = SimuleringOffentligTjenestepensjonSpecV3(
        foedselsdato = LocalDate.of(1963, 2, 24),
        aarligInntektFoerUttakBeloep = 1,
        gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV3(
            uttaksalder = SimuleringOffentligTjenestepensjonAlderV3(63, 0),
            aarligInntektVsaPensjonBeloep = 2
        ),
        heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV3(
            uttaksalder = SimuleringOffentligTjenestepensjonAlderV3(65, 11),
            aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV3(
                beloep = 3,
                sluttAlder = SimuleringOffentligTjenestepensjonAlderV3(70, 0)
            )
        ),
        utenlandsperiodeListe = listOf(
            UtenlandsoppholdV3(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 12, 31)),
            UtenlandsoppholdV3(LocalDate.of(2021, 1, 1), LocalDate.of(2022, 12, 31))
        ),
        epsHarPensjon = true,
        epsHarInntektOver2G = true,
        brukerBaOmAfp = true,
        erApoteker = true
    )

    should("map graded and full withdrawal with income and overlapping stays abroad") {
        TjenestepensjonSimuleringSpecMapperV3.fromDtoV3(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(LocalDate.of(2026, 3, 1), 2),
                        LoependeInntekt(LocalDate.of(2029, 2, 1), 3),
                        LoependeInntekt(LocalDate.of(2033, 4, 1), 0)
                    ),
                    aarIUtlandetEtter16 = 3,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = true
                )
    }

    should("start zero income at full withdrawal after graded withdrawal with income") {
        val result = TjenestepensjonSimuleringSpecMapperV3.fromDtoV3(
            spec.copy(
                heltUttak = spec.heltUttak.copy(aarligInntektVsaPensjon = null),
                erApoteker = null
            )
        )

        result.fremtidigeInntekter shouldBe listOf(
            LoependeInntekt(LocalDate.of(2026, 3, 1), 2),
            LoependeInntekt(LocalDate.of(2029, 2, 1), 0)
        )
        result.erApoteker shouldBe false
    }

    should("map full withdrawal without income and an open-ended stay abroad") {
        val result = TjenestepensjonSimuleringSpecMapperV3.fromDtoV3(
            spec.copy(
                gradertUttak = null,
                heltUttak = spec.heltUttak.copy(aarligInntektVsaPensjon = null),
                utenlandsperiodeListe = listOf(UtenlandsoppholdV3(LocalDate.of(2028, 2, 1), null)),
                epsHarPensjon = false,
                epsHarInntektOver2G = false,
                brukerBaOmAfp = false,
                erApoteker = false
            )
        )

        result shouldBe SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.of(1963, 2, 24),
            uttaksdato = LocalDate.of(2029, 2, 1),
            sisteInntekt = 1,
            fremtidigeInntekter = listOf(LoependeInntekt(LocalDate.of(2029, 2, 1), 0)),
            aarIUtlandetEtter16 = 1,
            brukerBaOmAfp = false,
            epsPensjon = false,
            eps2G = false,
            erApoteker = false
        )
    }
})
