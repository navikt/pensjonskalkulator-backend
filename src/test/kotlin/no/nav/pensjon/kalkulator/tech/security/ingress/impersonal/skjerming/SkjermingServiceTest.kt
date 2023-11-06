package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.SkjermingClient
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class SkjermingServiceTest {

    @Mock
    private lateinit var client: SkjermingClient

    @Test
    fun `personErTilgjengelig returns false when person er skjermet`() {
        `when`(client.personErTilgjengelig(pid)).thenReturn(false)
        assertFalse(SkjermingService(client).personErTilgjengelig(pid))
    }

    @Test
    fun `personErTilgjengelig returns true when person ikke er skjermet`() {
        `when`(client.personErTilgjengelig(pid)).thenReturn(true)
        assertTrue(SkjermingService(client).personErTilgjengelig(pid))
    }
}
