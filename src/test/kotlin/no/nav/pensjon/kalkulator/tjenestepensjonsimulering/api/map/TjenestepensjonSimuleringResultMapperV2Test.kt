package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.OffentligTjenestepensjonSimuleringsresultatDtoV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.SimuleringsresultatStatusV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TjenestepensjonSimuleringResultMapperV2Test{

    @Test
    fun `map all fields to dto and convert monthly to annual payout`() {
        val start = Alder(62, 0)
        val slutt = Alder(63, 0)
        val source = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(
                resultatType = ResultatType.OK,
                feilmelding = "feilmelding"
            ),
            simuleringsResultat = SimuleringsResultat(
                tpOrdning = "tpOrdningX",
                tpNummer = "1",
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

        val result: OffentligTjenestepensjonSimuleringsresultatDtoV2 = TjenestepensjonSimuleringResultMapperV2.toDtoV2(source)

        assertEquals(SimuleringsresultatStatusV2.OK, result.simuleringsresultatStatus)
        assertEquals("tpOrdningY", result.muligeTpLeverandoerListe[0])
        assertEquals("tpOrdningX", result.simulertTjenestepensjon?.tpLeverandoer)
        assertEquals("1", result.simulertTjenestepensjon?.tpNummer)
        assertEquals(start, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.startAlder)
        assertEquals(slutt, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.sluttAlder)
        assertEquals(1200, result.simulertTjenestepensjon?.simuleringsresultat?.utbetalingsperioder?.get(0)?.aarligUtbetaling)
        assertTrue(result.simulertTjenestepensjon?.simuleringsresultat?.betingetTjenestepensjonErInkludert!!)
    }
}