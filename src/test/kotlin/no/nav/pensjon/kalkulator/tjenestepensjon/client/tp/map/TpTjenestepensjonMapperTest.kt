package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tjenestepensjon.Forhold
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.Ytelse
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.*
import java.time.LocalDate

class TpTjenestepensjonMapperTest : ShouldSpec({

    context("TpApotekerDto") {
        should("map data transfer object to boolean") {
            TpTjenestepensjonMapper.fromDto(
                TpApotekerDto(
                    harLopendeForholdApotekerforeningen = true,
                    harAndreLopendeForhold = false
                )
            ) shouldBe true

            TpTjenestepensjonMapper.fromDto(
                TpApotekerDto(
                    harLopendeForholdApotekerforeningen = false,
                    harAndreLopendeForhold = true
                )
            ) shouldBe false

            TpTjenestepensjonMapper.fromDto(
                TpApotekerDto(
                    harLopendeForholdApotekerforeningen = null,
                    harAndreLopendeForhold = null
                )
            ) shouldBe false
        }
    }

    context("TpTjenestepensjonStatusDto") {
        should("map data transfer object to boolean") {
            TpTjenestepensjonMapper.fromDto(TpTjenestepensjonStatusDto(value = true)) shouldBe true
            TpTjenestepensjonMapper.fromDto(TpTjenestepensjonStatusDto(value = false)) shouldBe false
            TpTjenestepensjonMapper.fromDto(TpTjenestepensjonStatusDto(value = null)) shouldBe false
        }
    }

    context("TpTjenestepensjonDto") {
        should("map data transfer object to domain object") {
            val dto = TpTjenestepensjonDto(
                forhold = listOf(
                    TpForholdDto(
                        ordning = "3100",
                        ytelser = listOf(
                            TpYtelseDto(
                                type = "ALDER",
                                datoInnmeldtYtelseFom = LocalDate.of(2022, 7, 16),
                                datoYtelseIverksattFom = LocalDate.of(2023, 1, 1),
                                datoYtelseIverksattTom = LocalDate.of(2024, 12, 31)

                            ),
                            TpYtelseDto(
                                type = null,
                                datoInnmeldtYtelseFom = null,
                                datoYtelseIverksattFom = null,
                                datoYtelseIverksattTom = null
                            )
                        ),
                        datoSistOpptjening = LocalDate.of(2020, 1, 2)
                    )
                )
            )

            TpTjenestepensjonMapper.fromDto(dto) shouldBe
                    Tjenestepensjon(
                        forholdList = listOf(
                            Forhold(
                                ordning = "3100",
                                ytelser = listOf(
                                    Ytelse(
                                        type = "ALDER",
                                        datoInnmeldtYtelseFom = LocalDate.of(2022, 7, 16),
                                        datoYtelseIverksattFom = LocalDate.of(2023, 1, 1),
                                        datoYtelseIverksattTom = LocalDate.of(2024, 12, 31)
                                    ),
                                    Ytelse(
                                        type = "",
                                        datoInnmeldtYtelseFom = null,
                                        datoYtelseIverksattFom = null,
                                        datoYtelseIverksattTom = null
                                    )
                                ),
                                datoSistOpptjening = LocalDate.of(2020, 1, 2)
                            )
                        )
                    )
        }

        should("map nulls to default values") {
            TpTjenestepensjonMapper.fromDto(
                TpTjenestepensjonDto(
                    forhold = listOf(TpForholdDto(ordning = null, ytelser = null, datoSistOpptjening = null))
                )
            ) shouldBe
                    Tjenestepensjon(
                        forholdList = listOf(
                            Forhold(ordning = "", ytelser = emptyList(), datoSistOpptjening = null)
                        )
                    )
        }

        should("map null forhold-liste to empty list") {
            TpTjenestepensjonMapper.fromDto(TpTjenestepensjonDto(forhold = null)) shouldBe
                    Tjenestepensjon(forholdList = emptyList())
        }
    }
})
