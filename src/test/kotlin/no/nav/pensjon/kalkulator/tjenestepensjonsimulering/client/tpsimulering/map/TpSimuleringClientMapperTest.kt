package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.dto.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.tpsimulering.map.TpSimuleringClientMapper
import java.time.LocalDate

class TpSimuleringClientMapperTest : ShouldSpec({

    should("map 'simuler tjenestepensjon response' DTO to domain object") {
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
                        startAlder = Alder(aar = 62, maaneder = 0),
                        sluttAlder = Alder(aar = 63, maaneder = 0),
                        maanedligBeloep = 100
                    )
                ),
                betingetTjenestepensjonErInkludert = true
            ),
            relevanteTpOrdninger = listOf("tpOrdningY")
        )

        TpSimuleringClientMapper.fromDto(dto) shouldBe
                OffentligTjenestepensjonSimuleringsresultat(
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
                    tpOrdninger = listOf("tpOrdningY"),
                    serviceData = listOf()
                )
    }

    should("map 'simulering offentlig tjenestepensjon' specification to DTO") {
        val spec = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.of(1963, 2, 24),
            uttaksdato = LocalDate.of(2026, 3, 1),
            sisteInntekt = 1,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 3,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
            erApoteker = true
        )

        TpSimuleringClientMapper.toDto(spec, pid) shouldBe
                SimuleringOFTPSpecDto(
                    pid = pid.value,
                    foedselsdato = LocalDate.of(1963, 2, 24),
                    uttaksdato = LocalDate.of(2026, 3, 1),
                    sisteInntekt = 1,
                    aarIUtlandetEtter16 = 3,
                    brukerBaOmAfp = true,
                    epsPensjon = true,
                    eps2G = true,
                    fremtidigeInntekter = emptyList(),
                    erApoteker = true
                )
    }
})
