package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.ResultatType
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOFTPSpec
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
                utbetalingsperioder = listOf(
                    UtbetalingPerAar(
                        aar = 2021,
                        beloep = 100
                    )
                ),
                betingetTjenestepensjonErInkludert = true
            ),
            relevanteTpOrdninger = listOf("tpOrdningY")
        )

        val result: OFTPSimuleringsresultat = TpSimuleringClientMapper.fromDto(dto)

        assertEquals(ResultatType.OK, result.simuleringsResultatStatus.resultatType)
        assertEquals("tpOrdningY", result.tpOrdninger[0])
        assertEquals("tpOrdningX", result.simuleringsResultat?.tpOrdning)
        assertEquals(2021, result.simuleringsResultat?.perioder?.get(0)?.aar)
        assertEquals(100, result.simuleringsResultat?.perioder?.get(0)?.beloep)
        assertTrue(result.simuleringsResultat?.btpInkludert!!)
    }

    @Test
    fun `map to dto`() {
        val spec = SimuleringOFTPSpec(
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
}