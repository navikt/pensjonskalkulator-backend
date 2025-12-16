package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import java.time.LocalDate

class PenLoependeVedtakMapperTest : ShouldSpec({

    should("ignore pre-2025 offentlig AFP") {
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
            afpOffentlig = PenGjeldendeVedtakDto(fraOgMed = LocalDate.of(2021, 6, 15)),
            gjeldendeUttaksgradFom = LocalDate.of(2021, 1, 1)
        )

        val result = PenLoependeVedtakMapper.fromDto(dto)

        with(result) {
            pre2025OffentligAfp?.fom shouldBe LocalDate.of(2021, 6, 15)
            privatAfp?.fom shouldBe LocalDate.of(2021, 12, 31)

            with(loependeAlderspensjon!!) {
                grad shouldBe 1
                fom shouldBe LocalDate.of(2021, 1, 1)
                sivilstand shouldBe Sivilstand.SAMBOER
            }

            with(ufoeretrygd!!) {
                grad shouldBe 2
                fom shouldBe LocalDate.of(2021, 1, 2)
            }

            with(fremtidigAlderspensjon!!) {
                grad shouldBe 3
                fom shouldBe LocalDate.of(2022, 2, 2)
                sivilstand shouldBe Sivilstand.GIFT
            }
        }
    }

    should("ignore fremtidig vedtak med samme grad") {
        val dto = PenLoependeVedtakDto(
            alderspensjon = PenGjeldendeVedtakApDto(
                grad = 1,
                fraOgMed = LocalDate.of(2021, 1, 1),
                sivilstatus = "SAMB"
            ),
            alderspensjonIFremtid = PenGjeldendeVedtakApDto(
                grad = 1,
                fraOgMed = LocalDate.now().plusMonths(2),
                sivilstatus = "GIFT"
            ),
            ufoeretrygd = null,
            afpPrivat = null,
            afpOffentlig = null,
            gjeldendeUttaksgradFom = LocalDate.of(2021, 1, 1)
        )

        val result = PenLoependeVedtakMapper.fromDto(dto)

        with(result.loependeAlderspensjon!!) {
            grad shouldBe 1
            fom shouldBe LocalDate.of(2021, 1, 1)
            sivilstand shouldBe Sivilstand.SAMBOER
        }
    }
})
