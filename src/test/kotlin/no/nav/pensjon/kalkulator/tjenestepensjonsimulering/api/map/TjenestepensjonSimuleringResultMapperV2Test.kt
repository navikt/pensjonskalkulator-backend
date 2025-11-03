package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.OffentligTjenestepensjonSimuleringsresultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.ResultatType
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringsResultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringsResultatStatus
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.Utbetaling
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.OffentligTjenestepensjonSimuleringResultV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringsresultatStatusV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringResultMapperV2
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

        val result: OffentligTjenestepensjonSimuleringResultV2 = TjenestepensjonSimuleringResultMapperV2.toDtoV2(source)

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
