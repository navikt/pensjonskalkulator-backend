package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class PensjonsavtaleServiceTest {

    private lateinit var service: PensjonsavtaleService

    @Mock
    private lateinit var pensjonsavtaleClient: PensjonsavtaleClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = PensjonsavtaleService(pensjonsavtaleClient, pidGetter)
    }

    @Test
    fun `fetchAvtaler fetches avtaler`() {
        arrangePidAndClient()

        val avtaler = service.fetchAvtaler()

        val avtale = avtaler.liste[0]
        assertEquals("avtale1", avtale.navn)
        assertEquals(LocalDate.of(1992, 3, 4), avtale.fom)
        assertEquals(LocalDate.of(2010, 11, 12), avtale.tom)
    }

    private fun arrangePidAndClient() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(pensjonsavtaleClient.fetchAvtaler(pid)).thenReturn(avtaler())
    }

    private companion object {

        private val pid = Pid("12906498357")

        private fun avtaler() = Pensjonsavtaler(listOf(pensjonsavtale()))

        private fun pensjonsavtale() = Pensjonsavtale(
            "avtale1",
            LocalDate.of(1992, 3, 4),
            LocalDate.of(2010, 11, 12)
        )
    }
}
