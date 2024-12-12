package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpecV2
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period

class TjenestepensjonSimuleringSpecMapperV2Test {

    @Test
    fun `map from dto med utenlandsperioder, gradert og helt uttak med inntekter`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
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
                    fom = LocalDate.parse("2020-01-01"),
                    tom = LocalDate.parse("2021-12-31")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2022-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                )
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result: SimuleringOffentligTjenestepensjonSpecV2 = TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec)

        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(3, result.fremtidigeInntekter.size)
        assertEquals(2, result.fremtidigeInntekter[0].beloep)
        assertEquals(LocalDate.parse("2026-03-01"), result.fremtidigeInntekter[0].fom)
        assertEquals(3, result.fremtidigeInntekter[1].beloep)
        assertEquals(LocalDate.parse("2029-02-01"), result.fremtidigeInntekter[1].fom)
        assertEquals(0, result.fremtidigeInntekter[2].beloep)
        assertEquals(LocalDate.parse("2033-04-01"), result.fremtidigeInntekter[2].fom)
        assertEquals(3, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }

    @Test
    fun `map from dto med gradert uttak uten inntekt med helt uttak med inntekt`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(63, 0),
                aarligInntektVsaPensjonBeloep = null
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(65, 11),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    3,
                    SimuleringOffentligTjenestepensjonAlderV2(70, 0)
                )
            ),
            utenlandsperiodeListe = emptyList(),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result: SimuleringOffentligTjenestepensjonSpecV2 = TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec)

        assertEquals(2, result.fremtidigeInntekter.size)
        assertEquals(3, result.fremtidigeInntekter[0].beloep)
        assertEquals(LocalDate.parse("2029-02-01"), result.fremtidigeInntekter[0].fom)
        assertEquals(0, result.fremtidigeInntekter[1].beloep)
        assertEquals(LocalDate.parse("2033-04-01"), result.fremtidigeInntekter[1].fom)
    }

    @Test
    fun `map from dto med gradert uttak med inntekt og helt uttak uten inntekt`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(63, 0),
                aarligInntektVsaPensjonBeloep = 2
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(65, 11),
                aarligInntektVsaPensjon = null
            ),
            utenlandsperiodeListe = emptyList(),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result: SimuleringOffentligTjenestepensjonSpecV2 = TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec)

        assertEquals(2, result.fremtidigeInntekter.size)
        assertEquals(2, result.fremtidigeInntekter[0].beloep)
        assertEquals(LocalDate.parse("2026-03-01"), result.fremtidigeInntekter[0].fom)
        assertEquals(0, result.fremtidigeInntekter[1].beloep)
        assertEquals(LocalDate.parse("2029-02-01"), result.fremtidigeInntekter[1].fom)
    }

    @Test
    fun `map from dto med gradert uttak uten inntekt og helt uttak uten inntekt`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = SimuleringOffentligTjenestepensjonGradertUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(63, 0),
                aarligInntektVsaPensjonBeloep = null
            ),
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(65, 11),
                aarligInntektVsaPensjon = null
            ),
            utenlandsperiodeListe = emptyList(),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result: SimuleringOffentligTjenestepensjonSpecV2 = TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec)

        assertEquals(1, result.fremtidigeInntekter.size)
        assertEquals(0, result.fremtidigeInntekter[0].beloep)
        assertEquals(LocalDate.parse("2026-03-01"), result.fremtidigeInntekter[0].fom)
    }

    @Test
    fun `fromDto haandterer overlappende perioder`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            aarligInntektFoerUttakBeloep = 1,
            gradertUttak = null,
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(63, 0),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    3,
                    SimuleringOffentligTjenestepensjonAlderV2(70, 0)
                )
            ),
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2020-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2020-01-01"),
                    tom = LocalDate.parse("2021-12-31")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2020-03-31"),
                    tom = LocalDate.parse("2021-06-30")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2022-09-30"),
                    tom = LocalDate.parse("2022-12-30")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2021-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                ),
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result = TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec)

        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(3, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }

    @Test
    fun `fromDto haandterer overlappende perioder uten til og med dato`() {
        val fom = LocalDate.parse("2021-11-29")
        val antallAar = Period.between(fom, LocalDate.now().plusDays(1)).years //fom - inclusive, tom - inclusive too
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            gradertUttak = null,
            heltUttak = SimuleringOffentligTjenestepensjonHeltUttakV2(
                uttaksalder = SimuleringOffentligTjenestepensjonAlderV2(63, 0),
                aarligInntektVsaPensjon = SimuleringOffentligTjenestepensjonInntektV2(
                    3,
                    SimuleringOffentligTjenestepensjonAlderV2(70, 0)
                )
            ),
            aarligInntektFoerUttakBeloep = 1,
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV2(
                    fom = fom,
                    tom = LocalDate.parse("2022-02-01")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2022-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2022-12-01"),
                    tom = null
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2023-12-01"),
                    tom = null
                ),
                UtenlandsoppholdV2(
                    fom = LocalDate.parse("2024-12-01"),
                    tom = null
                ),
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result = TjenestepensjonSimuleringSpecMapperV2.fromDtoV2(spec)

        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(antallAar, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }
}