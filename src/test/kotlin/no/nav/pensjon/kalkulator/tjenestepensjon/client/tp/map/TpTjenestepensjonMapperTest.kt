package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tjenestepensjon.Forhold
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.Ytelse
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class TpTjenestepensjonMapperTest {

    @Test
    fun `apoteker 'fromDto' maps data transfer object to boolean`() {
        assertTrue(
            TpTjenestepensjonMapper.fromDto(
                TpApotekerDto(
                    harLopendeForholdApotekerforeningen = true,
                    harAndreLopendeForhold = false
                )
            )
        )
        assertFalse(
            TpTjenestepensjonMapper.fromDto(
                TpApotekerDto(
                    harLopendeForholdApotekerforeningen = false,
                    harAndreLopendeForhold = true
                )
            )
        )
        assertFalse(
            TpTjenestepensjonMapper.fromDto(
                TpApotekerDto(
                    harLopendeForholdApotekerforeningen = null,
                    harAndreLopendeForhold = null
                )
            )
        )
    }

    @Test
    fun `status 'fromDto' maps data transfer object to boolean`() {
        assertTrue(TpTjenestepensjonMapper.fromDto(TpTjenestepensjonStatusDto(value = true)))
        assertFalse(TpTjenestepensjonMapper.fromDto(TpTjenestepensjonStatusDto(value = false)))
        assertFalse(TpTjenestepensjonMapper.fromDto(TpTjenestepensjonStatusDto(value = null)))
    }

    @Test
    fun `tjenestepensjon 'fromDto' maps data transfer object to domain object`() {
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

        val expected = Tjenestepensjon(
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

        TpTjenestepensjonMapper.fromDto(dto) shouldBe expected
    }

    @Test
    fun `tjenestepensjon 'fromDto' maps nulls to default values`() {
        val dto = TpTjenestepensjonDto(
            forhold = listOf(TpForholdDto(ordning = null, ytelser = null, datoSistOpptjening = null))
        )

        val expected = Tjenestepensjon(
            forholdList = listOf(
                Forhold(ordning = "", ytelser = emptyList(), datoSistOpptjening = null)
            )
        )

        TpTjenestepensjonMapper.fromDto(dto) shouldBe expected
    }

    @Test
    fun `tjenestepensjon 'fromDto' maps null forhold-liste to empty list`() {
        TpTjenestepensjonMapper.fromDto(TpTjenestepensjonDto(forhold = null)) shouldBe Tjenestepensjon(emptyList())
    }
}

