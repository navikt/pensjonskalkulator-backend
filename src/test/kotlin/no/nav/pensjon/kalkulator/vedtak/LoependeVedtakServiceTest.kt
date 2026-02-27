package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import java.time.LocalDate

class LoependeVedtakServiceTest : ShouldSpec({

    should("hente løpende vedtak med eksisterende vedtak") {
        val vedtak = LoependeVedtakService(
            vedtakClient = arrangeVedtak(),
            pidGetter = mockk(relaxed = true)
        ).hentLoependeVedtak()

        with(vedtak) {
            with(loependeAlderspensjon!!) {
                grad shouldBe 1
                fom shouldBe LocalDate.of(2020, 10, 1)
                uttaksgradFom shouldBe LocalDate.of(2021, 1, 1)
                sivilstand shouldBe Sivilstand.UGIFT
            }
            with(fremtidigAlderspensjon!!) {
                grad shouldBe 3
                fom shouldBe LocalDate.of(2023, 10, 1)
                sivilstand shouldBe Sivilstand.GIFT
            }
            with(ufoeretrygd!!) {
                grad shouldBe 2
                fom shouldBe LocalDate.of(2021, 10, 1)
            }
            privatAfp?.fom shouldBe LocalDate.of(2022, 10, 1)
        }
    }
})

private fun arrangeVedtak(): LoependeVedtakClient =
    mockk<LoependeVedtakClient> {
        every {
            hentLoependeVedtak(any())
        } returns VedtakSamling(
            loependeAlderspensjon = LoependeAlderspensjon(
                grad = 1,
                fom = LocalDate.of(2020, 10, 1),
                uttaksgradFom = LocalDate.of(2021, 1, 1),
                sivilstand = Sivilstand.UGIFT,
            ),
            fremtidigAlderspensjon = FremtidigAlderspensjon(
                grad = 3,
                fom = LocalDate.of(2023, 10, 1),
                sivilstand = Sivilstand.GIFT
            ),
            ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.of(2021, 10, 1)),
            privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 10, 1))
        )
    }
