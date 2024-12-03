package no.nav.pensjon.kalkulator.avtale.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import org.junit.jupiter.api.Test

class PensjonsavtaleResultMapperV3Test {

    @Test
    fun `toDtoV3 maps from domain object to version 2 of data transfer object`() {
        PensjonsavtaleResultMapperV3.toDtoV3(
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
                PensjonsavtaleResultV3(
                    avtaler = listOf(
                        PensjonsavtaleV3(
                            produktbetegnelse = "p1",
                            kategori = AvtaleKategoriV3.INDIVIDUELL_ORDNING,
                            startAar = 67,
                            sluttAar = 77,
                            utbetalingsperioder = listOf(
                                UtbetalingsperiodeV3(
                                    startAlder = Alder(aar = 68, maaneder = 6),
                                    sluttAlder = Alder(aar = 78, maaneder = 5),
                                    aarligUtbetaling = 12000,
                                    grad = 80
                                )
                            )

                        )
                    ),
                    utilgjengeligeSelskap = listOf(SelskapV3(navn = "n1", heltUtilgjengelig = true))
                )
    }
}
