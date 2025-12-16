package no.nav.pensjon.kalkulator.ufoere

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.DateFactory.date
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.ufoere.client.UfoeregradClient
import no.nav.pensjon.kalkulator.ufoere.client.VedtakClient

class UfoerepensjonServiceTest : ShouldSpec({

    should("return 'true' when uførepensjonsvedtak exists") {
        val service = UfoerepensjonService(
            vedtakClient = arrangeVedtak(Sakstype.UFOEREPENSJON),
            ufoeregradClient = mockk(),
            pidGetter = arrangePid()
        )
        service.harLoependeUfoerepensjon(date) shouldBe true
    }

    should("return 'false' when no uførepensjonsvedtak exists") {
        val service = UfoerepensjonService(
            vedtakClient = arrangeVedtak(Sakstype.UNKNOWN),
            ufoeregradClient = mockk(),
            pidGetter = arrangePid()
        )

        service.harLoependeUfoerepensjon(date) shouldBe false
    }

    should("map existing uføregrad") {
        val service = UfoerepensjonService(
            vedtakClient = mockk(),
            ufoeregradClient = arrangeUfoeregrad60(),
            pidGetter = arrangePid()
        )

        service.hentUfoeregrad().uforegrad shouldBe 60
    }
})

private fun arrangePid(): PidGetter =
    mockk<PidGetter>().apply {
        every { pid() } returns pid
    }

private fun arrangeUfoeregrad60(): UfoeregradClient =
    mockk<UfoeregradClient>().apply {
        every { hentUfoeregrad(any()) } returns Ufoeregrad(60)
    }

private fun arrangeVedtak(sakstype: Sakstype) =
    mockk<VedtakClient>().apply {
        every { bestemGjeldendeVedtak(pid, date) } returns listOf(Vedtak(sakstype))
    }
