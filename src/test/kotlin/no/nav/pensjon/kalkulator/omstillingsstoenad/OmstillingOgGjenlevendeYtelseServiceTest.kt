package no.nav.pensjon.kalkulator.omstillingsstoenad

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.client.OmstillingsstoenadClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import java.time.LocalDate
import java.time.LocalDateTime

class OmstillingOgGjenlevendeYtelseServiceTest : ShouldSpec({

    val now = LocalDateTime.now()

    should("return 'true' when omstillingsstønad is received on given date") {
        val service = OmstillingsstoenadService(
            client = arrangeOmstillingsstoenad(dato = now.toLocalDate()),
            pidGetter = arrangePid(),
            timeProvider = arrangeTime(time = now)
        )

        service.mottarOmstillingsstoenad() shouldBe true
    }
})

private fun arrangeOmstillingsstoenad(dato: LocalDate): OmstillingsstoenadClient =
    mockk<OmstillingsstoenadClient>().apply {
        coEvery { mottarOmstillingsstoenad(pid, paaDato = dato) } returns true
    }

private fun arrangePid(): PidGetter =
    mockk<PidGetter>().apply {
        every { pid() } returns pid
    }

private fun arrangeTime(time: LocalDateTime): TimeProvider =
    mockk<TimeProvider>().apply {
        every { time() } returns time
    }
