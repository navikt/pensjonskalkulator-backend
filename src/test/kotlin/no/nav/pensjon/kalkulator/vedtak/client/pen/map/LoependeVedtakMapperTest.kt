package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import org.junit.jupiter.api.Test
import java.time.LocalDate

class LoependeVedtakMapperTest {

    @Test
    fun `fromDto should ignore pre-2025 offentlig AFP`() {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenGjeldendeVedtakApDto(
                grad = 1,
                fraOgMed = LocalDate.of(2021, 1, 1),
                sivilstatus = "SAMB"
            ),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                grad = 3,
                fraOgMed = LocalDate.of(2022, 2, 2),
                sivilstatus = "GIFT"
            ),
            ufoeretrygd = PenGjeldendeUfoeregradDto(grad = 2, fraOgMed = LocalDate.of(2021, 1, 2)),
            afpPrivat = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 12, 31)),
            afpOffentlig = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 6, 15))
        )

        val result = LoependeVedtakMapper.fromDto(dto)

        with(result) {
            afpOffentlig shouldBe null
            pre2025OffentligAfp?.fom shouldBe LocalDate.of(2021, 6, 15)
            afpPrivat?.fom shouldBe LocalDate.of(2021, 12, 31)

            with(alderspensjon!!) {
                grad shouldBe 1
                fom shouldBe LocalDate.of(2021, 1, 1)
                sivilstand.name shouldBe "SAMBOER"
            }

            with(ufoeretrygd!!) {
                grad shouldBe 2
                fom shouldBe LocalDate.of(2021, 1, 2)
            }

            with(fremtidigLoependeVedtakAp!!) {
                grad shouldBe 3
                fom shouldBe LocalDate.of(2022, 2, 2)
                sivilstand.name shouldBe "GIFT"
            }
        }
    }
}
