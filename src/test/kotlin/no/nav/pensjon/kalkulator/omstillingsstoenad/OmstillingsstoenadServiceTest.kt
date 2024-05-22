package no.nav.pensjon.kalkulator.omstillingsstoenad

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.client.OmstillingsstoenadClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
class OmstillingsstoenadServiceTest {

    private lateinit var service: OmstillingsstoenadService

    @Mock
    private lateinit var client: OmstillingsstoenadClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun initialize() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(timeProvider.time()).thenReturn(now)
        service = OmstillingsstoenadService(client, pidGetter, timeProvider)
    }

    @Test
    fun mottarOmstillingsstoenad() {
        `when`(client.mottarOmstillingsstoenad(pid, paaDato)).thenReturn(true)
        val resultat = service.mottarOmstillingsstoenad()
        assertTrue(resultat)
    }

    companion object {
        val now = LocalDateTime.now()
        val paaDato = now.toLocalDate()
    }
}