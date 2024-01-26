package no.nav.pensjon.kalkulator.sak

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SakServiceTest {

    @Mock
    private lateinit var client: SakClient

    @Mock
    private lateinit var pidGetter: PidGetter

    private lateinit var sakService: SakService

    @BeforeEach
    fun initialize() {
        `when`(pidGetter.pid()).thenReturn(pid)
        sakService = SakService(client, pidGetter)
    }

    @Test
    fun `'sakStatus' gir 'true' for loepende ufoeretrygd`() {
        `when`(client.fetchSaker(pid)).thenReturn(
            listOf(
                Sak(SakType.GENERELL, SakStatus.AVSLUTTET),
                Sak(SakType.UFOERETRYGD, SakStatus.LOEPENDE)
            )
        )
        val expected = RelevantSakStatus(harSak = true, sakType = SakType.UFOERETRYGD)

        sakService.sakStatus() shouldBe expected
    }

    @Test
    fun `'sakStatus' gir 'false' for avsluttet gjenlevendeytelse`() {
        `when`(client.fetchSaker(pid)).thenReturn(
            listOf(
                Sak(SakType.GENERELL, SakStatus.LOEPENDE),
                Sak(SakType.GJENLEVENDEYTELSE, SakStatus.AVSLUTTET)
            )
        )
        val expected = RelevantSakStatus(harSak = false, sakType = SakType.NONE)

        sakService.sakStatus() shouldBe expected
    }

    @Test
    fun `'sakStatus' gir 'false' for loepende irrelevante saker`() {
        `when`(client.fetchSaker(pid)).thenReturn(
            listOf(
                Sak(SakType.GENERELL, SakStatus.LOEPENDE),
                Sak(SakType.ALDERSPENSJON, SakStatus.LOEPENDE)
            )
        )
        val expected = RelevantSakStatus(harSak = false, sakType = SakType.NONE)

        sakService.sakStatus() shouldBe expected
    }
}
