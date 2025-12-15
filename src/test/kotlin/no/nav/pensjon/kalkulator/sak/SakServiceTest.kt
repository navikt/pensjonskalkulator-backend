package no.nav.pensjon.kalkulator.sak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.sak.client.SakClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter

class SakServiceTest : ShouldSpec({

    should("returnere 'true' for løpende uføretrygd") {
        SakService(
            sakClient = arrangeSaker(
                Sak(type = SakType.GENERELL, status = SakStatus.AVSLUTTET),
                Sak(type = SakType.UFOERETRYGD, status = SakStatus.LOEPENDE)
            ),
            pidGetter
        ).sakStatus() shouldBe RelevantSakStatus(harSak = true, sakType = SakType.UFOERETRYGD)
    }

    should("returnere 'false' for avsluttet gjenlevendeytelse") {
        SakService(
            sakClient = arrangeSaker(
                Sak(type = SakType.GENERELL, status = SakStatus.LOEPENDE),
                Sak(type = SakType.GJENLEVENDEYTELSE, status = SakStatus.AVSLUTTET)
            ),
            pidGetter
        ).sakStatus() shouldBe RelevantSakStatus(harSak = false, sakType = SakType.NONE)
    }

    should("returnere 'false' for løpende irrelevante saker") {

        SakService(
            sakClient = arrangeSaker(
                Sak(type = SakType.GENERELL, status = SakStatus.LOEPENDE),
                Sak(type = SakType.ALDERSPENSJON, status = SakStatus.LOEPENDE)
            ),
            pidGetter
        ).sakStatus() shouldBe RelevantSakStatus(
            harSak = false,
            sakType = SakType.NONE
        )
    }
})

private val pidGetter = mockk<PidGetter>(relaxed = true)

private fun arrangeSaker(vararg saker: Sak): SakClient =
    mockk<SakClient>().apply {
        every { fetchSaker(any()) } returns listOf(*saker)
    }
