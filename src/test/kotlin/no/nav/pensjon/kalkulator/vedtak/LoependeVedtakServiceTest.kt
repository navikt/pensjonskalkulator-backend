package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import java.time.LocalDate

class LoependeVedtakServiceTest : ShouldSpec({

    should("hente løpende vedtak med eksisterende vedtak") {
        LoependeVedtakService(
            vedtakClient = arrangeVedtak(),
            pidGetter = mockk(relaxed = true)
        ).hentLoependeVedtak() shouldBe vedtakSamling
    }
})

private val vedtakSamling =
    VedtakSamling(
        loependeAlderspensjon = LoependeAlderspensjon(
            grad = 1,
            fom = LocalDate.of(2020, 10, 1),
            uttaksgradFom = LocalDate.of(2021, 1, 1),
            sivilstatus = Sivilstatus.UGIFT,
        ),
        fremtidigAlderspensjon = FremtidigAlderspensjon(
            grad = 3,
            fom = LocalDate.of(2023, 10, 1),
            sivilstatus = Sivilstatus.GIFT
        ),
        ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.of(2021, 10, 1)),
        privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 10, 1)),
        avdoed = InformasjonOmAvdoed(
            pid = pid,
            doedsdato = LocalDate.of(2025, 6, 14),
            foersteAlderspensjonVirkningsdato = LocalDate.of(2021, 1, 1),
            aarligPensjonsgivendeInntektErMinst1G = true,
            harTilstrekkeligMedlemskapIFolketrygden = false,
            antallAarUtenlands = 3,
            erFlyktning = true
        )
    )

private fun arrangeVedtak(): LoependeVedtakClient =
    mockk<LoependeVedtakClient> {
        every {
            hentLoependeVedtak(any())
        } returns vedtakSamling
    }
