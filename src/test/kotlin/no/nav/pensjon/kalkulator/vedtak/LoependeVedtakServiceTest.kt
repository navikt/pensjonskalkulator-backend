package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class LoependeVedtakServiceTest {

    @Mock
    private lateinit var client: LoependeVedtakClient

    @Mock
    private lateinit var pidGetter: PidGetter

    private lateinit var service: LoependeVedtakService

    @BeforeEach
    fun initialize() {
        `when`(pidGetter.pid()).thenReturn(pid)
        service = LoependeVedtakService(client, pidGetter)
    }

    @Test
    fun `hent loepende vedtak med eksisterende vedtak`() {
        `when`(client.hentLoependeVedtak(pid)).thenReturn(
            LoependeVedtak(
                alderspensjon = LoependeAlderspensjonDetaljer(
                    grad = 1,
                    fom = LocalDate.parse("2020-10-01")
                ),
                fremtidigLoependeVedtakAp = true,
                ufoeretrygd = LoependeUfoeretrygdDetaljer(
                    grad = 2,
                    fom = LocalDate.parse("2021-10-01")
                ),
                afpPrivat = LoependeVedtakDetaljer(
                    fom = LocalDate.parse("2022-10-01")
                ),
                afpOffentlig = null
            )
        )

        val loependeVedtak = service.hentLoependeVedtak()

        with(loependeVedtak) {
            assertEquals(1, alderspensjon?.grad)
            assertEquals(LocalDate.parse("2020-10-01"), alderspensjon?.fom)
            assertTrue(loependeVedtak.fremtidigLoependeVedtakAp)
            assertEquals(2, ufoeretrygd?.grad)
            assertEquals(LocalDate.parse("2021-10-01"), ufoeretrygd?.fom)
            assertEquals(LocalDate.parse("2022-10-01"), afpPrivat?.fom)
            assertNull(afpOffentlig)
        }
    }
}