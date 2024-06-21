package no.nav.pensjon.kalkulator.avtale.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.junit.jupiter.api.Test

class PensjonsavtaleResultMapperV2Test {

    @Test
    fun `toDtoV2 maps from domain object to version 2 of data transfer object`() {
        PensjonsavtaleResultMapperV2.toDtoV2(
            Pensjonsavtaler(
                avtaler = listOf(
                    Pensjonsavtale(
                        produktbetegnelse = "p1",
                        kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
                        startalder = 67,
                        sluttalder = 77,
                        utbetalingsperioder = listOf(
                            Utbetalingsperiode(
                                startAlder = Alder(aar = 68, maaneder = 6),
                                sluttAlder = Alder(aar = 78, maaneder = 5),
                                aarligUtbetaling = 12000,
                                grad = Uttaksgrad.AATTI_PROSENT
                            )
                        )
                    )
                ),
                utilgjengeligeSelskap = listOf(Selskap(navn = "n1", heltUtilgjengelig = true))
            )
        ) shouldBe
                PensjonsavtaleResultV2(
                    avtaler = listOf(
                        PensjonsavtaleV2(
                            produktbetegnelse = "p1",
                            kategori = AvtaleKategoriV2.INDIVIDUELL_ORDNING,
                            startAar = 67,
                            sluttAar = 77,
                            utbetalingsperioder = listOf(
                                UtbetalingsperiodeV2(
                                    startAlder = Alder(aar = 68, maaneder = 6),
                                    sluttAlder = Alder(aar = 78, maaneder = 5),
                                    aarligUtbetaling = 12000,
                                    grad = 80
                                )
                            )

                        )
                    ),
                    utilgjengeligeSelskap = listOf(SelskapV2(navn = "n1", heltUtilgjengelig = true))
                )
    }
}
