package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.OffentligTjenestepensjonSimuleringsresultatDtoV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.SimuleringsresultatStatusV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TjenestepensjonSimuleringMapperTest {

    @Test
    fun `map all fields to dto`() {
        val source = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.OK,
                feilmelding = "feilmelding"
            ),
            simuleringsResultat = SimuleringsResultat(
                tpOrdning = "tpOrdningX",
                perioder = listOf(
                    Utbetaling(
                        aar = 2021,
                        beloep = 100
                    )
                ),
                betingetTjenestepensjonInkludert = true
            ),
            tpOrdninger = listOf("tpOrdningY")
        )

        val result: OffentligTjenestepensjonSimuleringsresultatDtoV1 = TjenestepensjonSimuleringResultMapperV1.toDto(source)

        assertEquals(SimuleringsresultatStatusV1.OK, result.simuleringsresultatStatus)
        assertEquals("tpOrdningY", result.muligeTpLeverandoerListe[0])
        assertEquals("tpOrdningX", result.simulertTjenestepensjon?.tpLeverandoer)
        assertEquals(2021, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.aar)
        assertEquals(100, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.beloep)
        assertTrue(result.simulertTjenestepensjon?.simuleringsresultat?.betingetTjenestepensjonErInkludert!!)
    }
}