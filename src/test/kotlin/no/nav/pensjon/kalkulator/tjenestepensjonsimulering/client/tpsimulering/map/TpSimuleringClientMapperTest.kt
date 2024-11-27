package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.OFTPSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.ResultatType
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.dto.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

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
}