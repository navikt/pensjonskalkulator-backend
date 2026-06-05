package no.nav.pensjon.kalkulator.sak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.sak.client.SakClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter

class SakServiceTest : ShouldSpec({

    should("returnere 'true' for løpende uføretrygd") {
        SakService(
            sakClient = arrangeSaker(
                Sak(sakId = 1, type = SakType.GENERELL, status = SakStatus.AVSLUTTET),
                Sak(sakId = 2, type = SakType.UFOERETRYGD, status = SakStatus.LOEPENDE)
            ),
            pidGetter
        ).sakStatus() shouldBe RelevantSakStatus(harSak = true, sakType = SakType.UFOERETRYGD)
    }

    should("returnere 'false' for avsluttet gjenlevendeytelse") {
        SakService(
            sakClient = arrangeSaker(
                Sak(sakId = 1, type = SakType.GENERELL, status = SakStatus.LOEPENDE),
                Sak(sakId = 2, type = SakType.GJENLEVENDEYTELSE, status = SakStatus.AVSLUTTET)
            ),
            pidGetter
        ).sakStatus() shouldBe RelevantSakStatus(harSak = false, sakType = SakType.NONE)
    }

    should("returnere 'false' for løpende irrelevante saker") {

        SakService(
            sakClient = arrangeSaker(
                Sak(sakId = 1, type = SakType.GENERELL, status = SakStatus.LOEPENDE),
                Sak(sakId = 2, type = SakType.ALDERSPENSJON, status = SakStatus.LOEPENDE)
            ),
            pidGetter
        ).sakStatus() shouldBe RelevantSakStatus(
            harSak = false,
            sakType = SakType.NONE
        )
    }

    should("hentEllerOpprettSak returnerer eksisterende alderspensjon sakId") {
        val sakClient = arrangeSaker(
            Sak(sakId = 1, type = SakType.GENERELL, status = SakStatus.LOEPENDE),
            Sak(sakId = 2, type = SakType.ALDERSPENSJON, status = SakStatus.OPPRETTET)
        )

        SakService(sakClient, pidGetter)
            .hentEllerOpprettSak(SakType.ALDERSPENSJON) shouldBe 2

        verify(exactly = 0) { sakClient.opprettNySak(any(), any()) }
    }

    should("hentEllerOpprettSak oppretter sak når ingen alderspensjon finnes") {
        val sakClient = arrangeSaker(
            Sak(sakId = 1, type = SakType.GENERELL, status = SakStatus.LOEPENDE)
        ).also {
            every { it.opprettNySak(any(), any()) } returns
                    Sak(sakId = 3, type = SakType.ALDERSPENSJON, status = SakStatus.OPPRETTET)
        }

        SakService(sakClient, pidGetter)
            .hentEllerOpprettSak(SakType.ALDERSPENSJON) shouldBe 3

        verify(exactly = 1) { sakClient.opprettNySak(any(), any()) }
    }

    should("hentEllerOpprettSak oppretter sak når ingen saker finnes") {
        val sakClient = arrangeSaker().also {
            every { it.opprettNySak(any(), any()) } returns
                    Sak(sakId = 4, type = SakType.ALDERSPENSJON, status = SakStatus.OPPRETTET)
        }

        SakService(sakClient, pidGetter)
            .hentEllerOpprettSak(SakType.ALDERSPENSJON) shouldBe 4

        verify(exactly = 1) { sakClient.opprettNySak(any(), any()) }
    }
})

private val pidGetter = mockk<PidGetter>(relaxed = true)

private fun arrangeSaker(vararg saker: Sak): SakClient =
    mockk<SakClient>().apply {
        every { fetchSaker(any()) } returns listOf(*saker)
    }
