package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringResultMapperV2

class TjenestepensjonSimuleringResultMapperV2Test : ShouldSpec({

    should("map 'offentlig tjenestepensjon simuleringsresultat' to DTO and convert monthly to annual payout") {
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
                        startAlder = Alder(aar = 62, maaneder = 0),
                        sluttAlder = Alder(aar = 63, maaneder = 0),
                        maanedligBeloep = 100
                    )
                ),
                betingetTjenestepensjonInkludert = true
            ),
            tpOrdninger = listOf("tpOrdningY")
        )

        TjenestepensjonSimuleringResultMapperV2.toDtoV2(source) shouldBe
                OffentligTjenestepensjonSimuleringResultV2(
                    simuleringsresultatStatus = SimuleringsresultatStatusV2.OK,
                    muligeTpLeverandoerListe = listOf("tpOrdningY"),
                    simulertTjenestepensjon = SimulertTjenestepensjonV2(
                        tpLeverandoer = "tpOrdningX",
                        tpNummer = "1",
                        simuleringsresultat = SimuleringsresultatV2(
                            utbetalingsperioder = listOf(
                                UtbetalingsperiodeV2(
                                    startAlder = Alder(aar = 62, maaneder = 0),
                                    sluttAlder = Alder(aar = 63, maaneder = 0),
                                    aarligUtbetaling = 1200,
                                    maanedligUtbetaling = 100
                                )
                            ),
                            betingetTjenestepensjonErInkludert = true
                        )
                    ),
                    serviceData = emptyList()
                )
    }
})
