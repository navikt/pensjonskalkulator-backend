package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.LoependeInntekt
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringSpecMapperV2
import java.time.LocalDate

class TjenestepensjonSimuleringSpecMapperV2Test : ShouldSpec({

    should("map from DTO med utenlandsperioder, gradert og helt uttak med inntekter") {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1963, 2, 24),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(63, 0),
                aarligInntektVsaPensjonBeloep = 2
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(65, 11),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    3,
                    SimuleringOffentligTjenestepensjonAlderV2(70, 0)
                )
            ),
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2020, 1, 1),
                    tom = LocalDate.of(2021, 12, 31)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2022, 1, 1),
                    tom = LocalDate.of(2022, 12, 31)
                )
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true,
            erApoteker = true
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(
                            fom = LocalDate.of(2026, 3, 1),
                            beloep = 2
                        ),
                        LoependeInntekt(
                            fom = LocalDate.of(2029, 2, 1),
                            beloep = 3
                        ),
                        LoependeInntekt(
                            fom = LocalDate.of(2033, 4, 1),
                            beloep = 0
                        )
                    ),
                    aarIUtlandetEtter16 = 3,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = true
                )
    }

    should("map from DTO med gradert uttak uten inntekt med helt uttak med inntekt") {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1963, 2, 24),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 63, maaneder = 0),
                aarligInntektVsaPensjonBeloep = null
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 65, maaneder = 11),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    beloep = 3,
                    sluttAlder = SimuleringOffentligTjenestepensjonAlderV2(aar = 70, maaneder = 0)
                )
            ),
            utenlandsperiodeListe = emptyList(),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true,
            erApoteker = false
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(
                            fom = LocalDate.of(2029, 2, 1),
                            beloep = 3
                        ),
                        LoependeInntekt(
                            fom = LocalDate.of(2033, 4, 1),
                            beloep = 0
                        )
                    ),
                    aarIUtlandetEtter16 = 0,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = false
                )
    }

    should("map from DTO med gradert uttak med inntekt og helt uttak uten inntekt") {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1963, 2, 24),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 63, maaneder = 0),
                aarligInntektVsaPensjonBeloep = 2
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 65, maaneder = 11),
                aarligInntektVsaPensjon = null
            ),
            utenlandsperiodeListe = emptyList(),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true,
            erApoteker = null
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(
                            fom = LocalDate.of(2026, 3, 1),
                            beloep = 2
                        ),
                        LoependeInntekt(
                            fom = LocalDate.of(2029, 2, 1),
                            beloep = 0
                        )
                    ),
                    aarIUtlandetEtter16 = 0,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = false
                )
    }

    should("map from DTO med gradert uttak uten inntekt og helt uttak uten inntekt") {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1963, 2, 24),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 63, maaneder = 0),
                aarligInntektVsaPensjonBeloep = null
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 65, maaneder = 11),
                aarligInntektVsaPensjon = null
            ),
            utenlandsperiodeListe = emptyList(),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true,
            erApoteker = false
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(
                            fom = LocalDate.of(2026, 3, 1),
                            beloep = 0
                        )
                    ),
                    aarIUtlandetEtter16 = 0,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = false
                )
    }

    should("håndtere overlappende perioder") {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1963, 2, 24),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = null,
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 63, maaneder = 0),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    beloep = 3,
                    sluttAlder = SimuleringOffentligTjenestepensjonAlderV2(aar = 70, maaneder = 0)
                )
            ),
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2020, 1, 1),
                    tom = LocalDate.of(2022, 12, 31)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2020, 1, 1),
                    tom = LocalDate.of(2021, 12, 31)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2020, 3, 31),
                    tom = LocalDate.of(2021, 6, 30)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2022, 9, 30),
                    tom = LocalDate.of(2022, 12, 30)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2021, 1, 1),
                    tom = LocalDate.of(2022, 12, 31)
                ),
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true,
            erApoteker = true
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(
                            fom = LocalDate.of(2026, 3, 1),
                            beloep = 3
                        ),
                        LoependeInntekt(
                            fom = LocalDate.of(2033, 4, 1),
                            beloep = 0
                        )
                    ),
                    aarIUtlandetEtter16 = 3,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = true
                )
    }

    should("håndtere overlappende perioder uten til og med dato") {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.of(1963, 2, 24),
            gradertUttak = null,
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 63, maaneder = 0),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    beloep = 3,
                    sluttAlder = SimuleringOffentligTjenestepensjonAlderV2(aar = 70, maaneder = 0)
                )
            ),
            aarligInntektFoerUttakBeloep = 1,
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2021, 11, 29),
                    tom = LocalDate.of(2022, 2, 1)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2022, 1, 1),
                    tom = LocalDate.of(2022, 12, 31)
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2022, 12, 1),
                    tom = null
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2023, 12, 1),
                    tom = null
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.of(2024, 12, 1),
                    tom = null
                )
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true,
            erApoteker = false
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec) shouldBe
                SimuleringOffentligTjenestepensjonSpec(
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    fremtidigeInntekter = listOf(
                        LoependeInntekt(
                            fom = LocalDate.of(2026, 3, 1),
                            beloep = 3
                        ),
                        LoependeInntekt(
                            fom = LocalDate.of(2033, 4, 1),
                            beloep = 0
                        )
                    ),
                    aarIUtlandetEtter16 = 4, // antall år mellom 2021-11-29 og 2026-03-02 (dagen etter uttaksdato)
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    erApoteker = false
                )
    }

    should("håndtere fremtidig varig utenlandsopphold") {
        val idag = LocalDate.now()
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = idag.minusYears(50).minusDays(1),
            gradertUttak = null,
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                // starter uttak om 13 år:
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(aar = 63, maaneder = 0),
                aarligInntektVsaPensjon = null
            ),
            aarligInntektFoerUttakBeloep = 1,
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV2(
                    // flytter til utlandet om 10 år, dvs. 3 år før uttak:
                    fom = idag.plusYears(10),
                    tom = null
                )
            ),
            epsHarPensjon = false,
            epsHarInntektOver2G = false,
            brukerBaOmAfp = false,
            erApoteker = false
        )

        TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec).aarIUtlandetEtter16 shouldBe 3
    }
})
