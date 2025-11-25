package no.nav.pensjon.kalkulator.tjenestepensjon

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.web.EgressException
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
        `when`(client.tjenestepensjonsforhold(pid)).thenReturn(tjenestepensjonMedMedlemskap())
        val result = service.hentMedlemskapITjenestepensjonsordninger()
        assertEquals(listOf("Maritim pensjonskasse", "Statens pensjonskasse", "Kommunal Landspensjonskasse"), result)
    }

    @Test
    fun `hentAfpOffentligLivsvarigDetaljer returnerer korrekte detaljer naar bruker har en ordning`() {
        val tpNr = "3010"
        val expectedResult = AfpOffentligLivsvarigResult(afpStatus = true, maanedligBeloep = 15000)
        val expectedUttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)

        `when`(client.afpOffentligLivsvarigTpNummerListe(pid)).thenReturn(listOf(tpNr))
        `when`(client.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, expectedUttaksdato)).thenReturn(expectedResult)

        val result = service.hentAfpOffentligLivsvarigDetaljer()

        assertEquals(expectedResult, result)
        verify(client).afpOffentligLivsvarigTpNummerListe(pid)
        verify(client).hentAfpOffentligLivsvarigDetaljer(pid, tpNr, expectedUttaksdato)
    }

    @Test
    fun `hentAfpOffentligLivsvarigDetaljer kaster exception naar bruker ikke har noen ordninger`() {
        `when`(client.afpOffentligLivsvarigTpNummerListe(pid)).thenReturn(emptyList())

        val result = service.hentAfpOffentligLivsvarigDetaljer()

        assertEquals(AfpOffentligLivsvarigResult(afpStatus = null, maanedligBeloep = null), result)
        verify(client).afpOffentligLivsvarigTpNummerListe(pid)
        verifyNoMoreInteractions(client)
    }

    @Test
    fun `hentAfpOffentligLivsvarigDetaljer kaster exception naar bruker har flere ordninger`() {
        val tpNumre = listOf("3010", "3020", "3030")
        `when`(client.afpOffentligLivsvarigTpNummerListe(pid)).thenReturn(tpNumre)

        val exception = assertThrows(EgressException::class.java) {
            service.hentAfpOffentligLivsvarigDetaljer()
        }

        assertTrue(exception.message!!.contains("Bruker har flere AFP offentlig livsvarig ordninger"))
        assertTrue(exception.message!!.contains("(3)"))
        verify(client).afpOffentligLivsvarigTpNummerListe(pid)
        verifyNoMoreInteractions(client)
    }

    @Test
    fun `hentAfpOffentligLivsvarigDetaljer bruker neste maaned som uttaksdato`() {
        val tpNr = "3010"
        val expectedResult = AfpOffentligLivsvarigResult(afpStatus = false, maanedligBeloep = null)
        val expectedUttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)

        `when`(client.afpOffentligLivsvarigTpNummerListe(pid)).thenReturn(listOf(tpNr))
        `when`(client.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, expectedUttaksdato)).thenReturn(expectedResult)

        service.hentAfpOffentligLivsvarigDetaljer()

        verify(client).hentAfpOffentligLivsvarigDetaljer(pid, tpNr, expectedUttaksdato)
    }


    private companion object {
        private fun tjenestepensjon() = Tjenestepensjon(forholdList = listOf(forhold()))
        private fun tjenestepensjonMedMedlemskap() = Tjenestepensjonsforhold(
            tpOrdninger = listOf(
                "Maritim pensjonskasse",
                "Statens pensjonskasse",
                "Kommunal Landspensjonskasse"
            )
        )

        private fun forhold(tpOrdning: String = "") =
            Forhold(ordning = tpOrdning, ytelser = emptyList(), datoSistOpptjening = null)
    }
}
