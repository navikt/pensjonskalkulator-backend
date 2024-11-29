package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV2
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class TjenestepensjonSimuleringSpecMapperV2Test {

    @Test
    fun `map from dto med utenlandsperioder`() {
        val spec = IngressSimuleringOFTPSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            uttaksalder = Alder(63, 0),
            aarligInntektFoerUttakBeloep = 1,
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

        val result = TjenestepensjonSimuleringSpecMapperV2.fromDto(spec)

        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(3, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }
}