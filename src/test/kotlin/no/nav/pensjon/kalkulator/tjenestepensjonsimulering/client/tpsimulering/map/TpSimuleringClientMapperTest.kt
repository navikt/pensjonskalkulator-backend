package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.ResultatType
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class TpSimuleringClientMapperTest {

    @Test
    fun `map all fields from dto`() {
        val dto = SimulerTjenestepensjonResponseDto(
            simuleringsResultatStatus = SimuleringsResultatStatusDto(
                resultatType = ResultatTypeDto.SUCCESS,
                feilmelding = "feilmelding"
            ),
            simuleringsResultat = SimuleringsResultatDto(
                tpLeverandoer = "tpOrdningX",
                tpNummer = "1",
                utbetalingsperioder = listOf(
                    UtbetalingPerAlder(
                        startAlder = Alder(62, 0),
                        sluttAlder = Alder(63, 0),
                        maanedligBeloep = 100
                    )
                ),
                betingetTjenestepensjonErInkludert = true
            ),
            relevanteTpOrdninger = listOf("tpOrdningY")
        )

        val result: OffentligTjenestepensjonSimuleringsresultat = TpSimuleringClientMapper.fromDto(dto)

        assertEquals(ResultatType.OK, result.simuleringsResultatStatus.resultatType)
        assertEquals("tpOrdningY", result.tpOrdninger[0])
        assertEquals("tpOrdningX", result.simuleringsResultat?.tpOrdning)
        assertEquals("1", result.simuleringsResultat?.tpNummer)
        assertEquals(dto.simuleringsResultat!!.utbetalingsperioder[0].startAlder, result.simuleringsResultat?.perioder?.get(0)?.startAlder)
        assertEquals(dto.simuleringsResultat!!.utbetalingsperioder[0].sluttAlder, result.simuleringsResultat?.perioder?.get(0)?.sluttAlder)
        assertEquals(100, result.simuleringsResultat?.perioder?.get(0)?.maanedligBeloep)
        assertTrue(result.simuleringsResultat?.betingetTjenestepensjonInkludert!!)
    }

    @Test
    fun `map to dto`() {
        val spec = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1963-02-24"),
            uttaksdato = LocalDate.parse("2026-03-01"),
            sisteInntekt = 1,
            aarIUtlandetEtter16 = 3,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true
        )

        val result: SimuleringOFTPSpecDto = TpSimuleringClientMapper.toDto(spec, pid)

        assertEquals(pid.value, result.pid)
        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(3, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
    }

    @Test
    fun `map to dto v2`() {
        val spec = SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = LocalDate.parse("1963-02-24"),
            uttaksdato = LocalDate.parse("2026-03-01"),
            sisteInntekt = 1,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 3,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
            erApoteker = true
        )

        val result: SimuleringOFTPSpecDto = TpSimuleringClientMapper.toDto(spec, pid)

        assertEquals(pid.value, result.pid)
        assertEquals(LocalDate.parse("1963-02-24"), result.foedselsdato)
        assertEquals(LocalDate.parse("2026-03-01"), result.uttaksdato)
        assertEquals(1, result.sisteInntekt)
        assertEquals(3, result.aarIUtlandetEtter16)
        assertEquals(true, result.brukerBaOmAfp)
        assertEquals(true, result.epsPensjon)
        assertEquals(true, result.eps2G)
        assertEquals(true, result.erApoteker)
    }
}
