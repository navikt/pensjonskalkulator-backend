package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.client.tpsimulering.dto.*
import java.time.LocalDate
import java.time.ZoneId

class TpSimuleringFoer1963ClientMapperTest : ShouldSpec({

    fun localDateToEpochMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli()

    should("map alderTom riktig når fom og tom er i samme år") {
        val foedselsdato = LocalDate.of(1960, 3, 15)
        val datoFom = LocalDate.of(2025, 4, 1)
        val datoTom = LocalDate.of(2025, 10, 1)

        val dto = SimulerTjenestepensjonFoer1963ResponseDto(
            simulertPensjonListe = listOf(
                SimulertPensjon(
                    tpnr = "1",
                    navnOrdning = "test",
                    inkluderteOrdninger = null,
                    leverandorUrl = null,
                    utbetalingsperioder = listOf(
                        Utbetalingsperiode(
                            datoFom = localDateToEpochMillis(datoFom),
                            datoTom = localDateToEpochMillis(datoTom),
                            grad = 100,
                            arligUtbetaling = 120000.0,
                            ytelsekode = YtelsekodeFoer1963Dto.AP,
                            mangelfullSimuleringkode = null
                        )
                    )
                )
            ),
            feilkode = null,
            relevanteTpOrdninger = emptyList()
        )

        val result = TpSimuleringFoer1963ClientMapper.fromDto(dto, foedselsdato)
        val periode = result.utbetalingsperioder.first()

        periode.alderFom shouldBe Alder(aar = 65, maaneder = 0)
        periode.alderTom shouldBe Alder(aar = 65, maaneder = 6)
    }

    should("map alderTom riktig når fom og tom er i forskjellige år") {
        val foedselsdato = LocalDate.of(1960, 3, 15)
        val datoFom = LocalDate.of(2025, 4, 1)
        val datoTom = LocalDate.of(2026, 10, 1)

        val dto = SimulerTjenestepensjonFoer1963ResponseDto(
            simulertPensjonListe = listOf(
                SimulertPensjon(
                    tpnr = "1",
                    navnOrdning = "test",
                    inkluderteOrdninger = null,
                    leverandorUrl = null,
                    utbetalingsperioder = listOf(
                        Utbetalingsperiode(
                            datoFom = localDateToEpochMillis(datoFom),
                            datoTom = localDateToEpochMillis(datoTom),
                            grad = 100,
                            arligUtbetaling = 120000.0,
                            ytelsekode = YtelsekodeFoer1963Dto.AP,
                            mangelfullSimuleringkode = null
                        )
                    )
                )
            ),
            feilkode = null,
            relevanteTpOrdninger = emptyList()
        )

        val result = TpSimuleringFoer1963ClientMapper.fromDto(dto, foedselsdato)
        val periode = result.utbetalingsperioder.first()

        periode.alderFom shouldBe Alder(aar = 65, maaneder = 0)
        periode.alderTom shouldBe Alder(aar = 66, maaneder = 6)
    }
})
