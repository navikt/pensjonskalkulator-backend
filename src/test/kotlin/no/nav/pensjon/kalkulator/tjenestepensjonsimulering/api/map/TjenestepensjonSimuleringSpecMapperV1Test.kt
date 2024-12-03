package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOffentligTjenestepensjonSpecV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV1
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate
import java.time.Period

class TjenestepensjonSimuleringSpecMapperV1Test {

    @Test
    fun `map from dto med utenlandsperioder`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV1(
            foedselsdato = LocalDate.parse("1963-02-24"),
            uttaksalder = Alder(63, 0),
            aarligInntektFoerUttakBeloep = 1,
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2020-01-01"),
                    tom = LocalDate.parse("2021-12-31")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2022-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                )
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result = TjenestepensjonSimuleringSpecMapperV1.fromDto(spec)

        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(3, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }

    @Test
    fun `fromDto haandterer overlappende perioder`() {
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV1(
            foedselsdato = LocalDate.parse("1963-02-24"),
            uttaksalder = Alder(63, 0),
            aarligInntektFoerUttakBeloep = 1,
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2020-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2020-01-01"),
                    tom = LocalDate.parse("2021-12-31")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2020-03-31"),
                    tom = LocalDate.parse("2021-06-30")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2022-09-30"),
                    tom = LocalDate.parse("2022-12-30")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2021-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                ),
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result = TjenestepensjonSimuleringSpecMapperV1.fromDto(spec)

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
        val spec = IngressSimuleringOffentligTjenestepensjonSpecV1(
            foedselsdato = LocalDate.parse("1963-02-24"),
            uttaksalder = Alder(63, 0),
            aarligInntektFoerUttakBeloep = 1,
            utenlandsperiodeListe = listOf(
                UtenlandsoppholdV1(
                    fom = fom,
                    tom = LocalDate.parse("2022-02-01")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2022-01-01"),
                    tom = LocalDate.parse("2022-12-31")
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2022-12-01"),
                    tom = null
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2023-12-01"),
                    tom = null
                ),
                UtenlandsoppholdV1(
                    fom = LocalDate.parse("2024-12-01"),
                    tom = null
                ),
            ),
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val result = TjenestepensjonSimuleringSpecMapperV1.fromDto(spec)

        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(antallAar, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }
}