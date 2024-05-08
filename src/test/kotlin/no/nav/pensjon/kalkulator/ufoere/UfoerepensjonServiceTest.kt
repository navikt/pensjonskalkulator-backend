package no.nav.pensjon.kalkulator.ufoere

import no.nav.pensjon.kalkulator.mock.DateFactory.date
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.ufoere.client.UfoeregradClient
import no.nav.pensjon.kalkulator.ufoere.client.VedtakClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class UfoerepensjonServiceTest {

    private lateinit var service: UfoerepensjonService

    @Mock
    private lateinit var vedtakClient: VedtakClient

    @Mock
    private lateinit var ufoeregradClient: UfoeregradClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = UfoerepensjonService(vedtakClient, ufoeregradClient, pidGetter)
    }

    @Test
    fun `harLoependeUfoerepensjon is true when ufoerepensjonsvedtak exists`() {
        arrangePidAndResultat(Sakstype.UFOEREPENSJON)
        val result = service.harLoependeUfoerepensjon(date)
        assertTrue(result)
    }

    @Test
    fun `harLoependeUfoerepensjon is false when no ufoerepensjonsvedtak exists`() {
        arrangePidAndResultat(Sakstype.UNKNOWN)
        val result = service.harLoependeUfoerepensjon(date)
        assertFalse(result)
    }

    @Test
    fun `Existing ufoeregrad is mapped and returned`() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(ufoeregradClient.hentUfoeregrad(pid)).thenReturn(Ufoeregrad(60))

        val result = service.hentUfoeregrad()
        assertEquals(60, result.uforegrad)
    }

    private fun arrangePidAndResultat(sakstype: Sakstype) {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(vedtakClient.bestemGjeldendeVedtak(pid, date)).thenReturn(listOf(Vedtak(sakstype)))
    }
}
