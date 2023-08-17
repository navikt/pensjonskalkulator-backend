package no.nav.pensjon.kalkulator.tjenestepensjon

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class TjenestepensjonServiceTest {

    private lateinit var service: TjenestepensjonService

    @Mock
    private lateinit var client: TjenestepensjonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = TjenestepensjonService(client, pidGetter) { dato }
    }

    @Test
    fun `harTjenestepensjonsforhold uses specified inntekt and sivilstand`() {
        arrangePidAndResultat()
        val result = service.harTjenestepensjonsforhold()
        assertTrue(result)
    }

    private fun arrangePidAndResultat() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(client.harTjenestepensjonsforhold(pid, dato)).thenReturn(true)
    }

    private companion object {
        private val dato = LocalDate.of(2023, 1, 1)
    }
}