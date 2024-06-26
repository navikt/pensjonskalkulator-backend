package no.nav.pensjon.kalkulator.tjenestepensjon

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class TjenestepensjonServiceTest {

    private lateinit var service: TjenestepensjonService

    @Mock
    private lateinit var client: TjenestepensjonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var featureToggleService: FeatureToggleService

    @BeforeEach
    fun initialize() {
        `when`(pidGetter.pid()).thenReturn(pid)
        service = TjenestepensjonService(client, pidGetter, featureToggleService)
    }

    @Test
    fun `'erApoteker' gir 'true' naar ekstern tjeneste gir 'true'`() {
        `when`(client.erApoteker(pid)).thenReturn(true)
        assertTrue(service.erApoteker())
    }

    @Test
    fun `'harTjenestepensjonsforhold' gir 'true' naar ekstern tjeneste gir tjenestepensjonsforhold`() {
        `when`(client.tjenestepensjon(pid)).thenReturn(tjenestepensjon())
        val result = service.harTjenestepensjonsforhold()
        assertTrue(result)
    }

    @Test
    fun `gir liste over alle medlemskap i tp-ordninger`() {
        `when`(client.tjenestepensjon(pid)).thenReturn(tjenestepensjonMedMedlemskap())
        val result = service.hentMedlemskapITjenestepensjonsordninger()
        assertEquals(listOf("Maritim pensjonskasse", "Statens pensjonskasse", "Kommunal Landspensjonskasse"), result)
    }


    private companion object {
        private fun tjenestepensjon() = Tjenestepensjon(forholdList = listOf(forhold()))
        private fun tjenestepensjonMedMedlemskap() = Tjenestepensjon(
            forholdList = listOf(
                forhold("Maritim pensjonskasse"),
                forhold("Statens pensjonskasse"),
                forhold("Kommunal Landspensjonskasse")
            )
        )

        private fun forhold(tpOrdning: String = "") =
            Forhold(ordning = tpOrdning, ytelser = emptyList(), datoSistOpptjening = null)
    }
}
