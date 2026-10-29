package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map.TjenestepensjonSimuleringResultMapperV3

class TjenestepensjonSimuleringResultMapperV3Test : ShouldSpec({

    should("map provider and pension scheme separately and convert monthly to annual payout") {
        val source = OffentligTjenestepensjonSimuleringsresultat(
            simuleringsResultatStatus = SimuleringsResultatStatus(ResultatType.OK),
            simuleringsResultat = SimuleringsResultat(
                tpLeverandoerNavn = "tpLeverandoerX",
                tpOrdning = TpOrdning(tpOrdningNavn = "tpOrdningX", tpNummer = "1"),
                perioder = listOf(
                    Utbetaling(
                        startAlder = Alder(aar = 62, maaneder = 0),
                        sluttAlder = Alder(aar = 63, maaneder = 0),
                        maanedligBeloep = 100
                    ),
                    Utbetaling(
                        startAlder = Alder(aar = 63, maaneder = 1),
                        sluttAlder = null,
                        maanedligBeloep = 200
                    )
                ),
                betingetTjenestepensjonInkludert = true
            ),
            tpOrdninger = listOf(
                TpOrdning(tpOrdningNavn = "tpOrdningY", tpNummer = "2"),
                TpOrdning(tpOrdningNavn = "tpOrdningZ", tpNummer = "3")
            ),
            serviceData = listOf("serviceData")
        )

        TjenestepensjonSimuleringResultMapperV3.toDtoV3(source) shouldBe
                OffentligTjenestepensjonSimuleringResultV3(
                    simuleringsresultatStatus = SimuleringsresultatStatusV3.OK,
                    muligeTpLeverandoerListe = listOf(
                        TpOrdningV3(tpNavn = "tpOrdningY", tpNummer = "2"),
                        TpOrdningV3(tpNavn = "tpOrdningZ", tpNummer = "3")
                    ),
                    simulertTjenestepensjon = SimulertTjenestepensjonV3(
                        tpLeverandoer = "tpLeverandoerX",
                        tpOrdning = TpOrdningV3(tpNavn = "tpOrdningX", tpNummer = "1"),
                        simuleringsresultat = SimuleringsresultatV3(
                            utbetalingsperioder = listOf(
                                UtbetalingsperiodeV3(
                                    startAlder = Alder(aar = 62, maaneder = 0),
                                    sluttAlder = Alder(aar = 63, maaneder = 0),
                                    aarligUtbetaling = 1200,
                                    maanedligUtbetaling = 100
                                ),
                                UtbetalingsperiodeV3(
                                    startAlder = Alder(aar = 63, maaneder = 1),
                                    sluttAlder = null,
                                    aarligUtbetaling = 2400,
                                    maanedligUtbetaling = 200
                                )
                            ),
                            betingetTjenestepensjonErInkludert = true
                        )
                    ),
                    serviceData = listOf("serviceData")
                )
    }

    listOf(
        ResultatType.IKKE_MEDLEM to SimuleringsresultatStatusV3.BRUKER_ER_IKKE_MEDLEM_AV_TP_ORDNING,
        ResultatType.TP_ORDNING_STOETTES_IKKE to SimuleringsresultatStatusV3.TP_ORDNING_STOETTES_IKKE,
        ResultatType.TOM_RESPONS to SimuleringsresultatStatusV3.TOM_SIMULERING_FRA_TP_ORDNING,
        ResultatType.TEKNISK_FEIL to SimuleringsresultatStatusV3.TEKNISK_FEIL
    ).forEach { (resultatType, status) ->
        should("map $resultatType without a simulation result") {
            val source = OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(resultatType),
                tpOrdninger = listOf(TpOrdning(tpOrdningNavn = "tpOrdningX", tpNummer = "1"))
            )

            TjenestepensjonSimuleringResultMapperV3.toDtoV3(source) shouldBe
                    OffentligTjenestepensjonSimuleringResultV3(
                        simuleringsresultatStatus = status,
                        muligeTpLeverandoerListe = listOf(TpOrdningV3(tpNavn = "tpOrdningX", tpNummer = "1")),
                        simulertTjenestepensjon = null,
                        serviceData = emptyList()
                    )
        }
    }
})
