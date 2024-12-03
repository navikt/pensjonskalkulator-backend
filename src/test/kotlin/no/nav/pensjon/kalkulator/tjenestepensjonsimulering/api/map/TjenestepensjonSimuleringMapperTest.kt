package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.OffentligTjenestepensjonSimuleringsresultatDtoV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.SimuleringsresultatStatusV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TjenestepensjonSimuleringMapperTest {

    @Test
    fun `map all fields to dto`() {
        val start = Alder(62, 0)
        val slutt = Alder(63, 0)
        val source = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.OK,
                feilmelding = "feilmelding"
            ),
            simuleringsResultat = SimuleringsResultat(
                tpOrdning = "tpOrdningX",
                perioder = listOf(
                    Utbetaling(
                        startAlder = start,
                        sluttAlder = slutt,
                        maanedligBeloep = 100
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
        assertEquals(start, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.startAlder)
        assertEquals(slutt, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.sluttAlder)
        assertEquals(100, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.aarligUtbetaling)
        assertTrue(result.simulertTjenestepensjon?.simuleringsresultat?.betingetTjenestepensjonErInkludert!!)
    }
}