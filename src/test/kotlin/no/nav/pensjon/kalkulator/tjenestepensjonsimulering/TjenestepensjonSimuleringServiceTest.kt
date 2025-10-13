package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.LoependeInntekt
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygdDetaljer
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakDetaljer
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
class TjenestepensjonSimuleringServiceTest {

    private lateinit var service: TjenestepensjonSimuleringService

    @Mock
    private lateinit var tjenestepensjonSimuleringClient: TjenestepensjonSimuleringClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var featureToggleService: FeatureToggleService

    @Mock
    private lateinit var loependeVedtakService: LoependeVedtakService

    @Mock
    private lateinit var ekskluderingFacade: EkskluderingFacade

    @BeforeEach
    fun initialize() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(LoependeVedtak(null, null, null, null, null, null))
        `when`(ekskluderingFacade.apotekerEkskludering()).thenReturn(EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE))
        service = TjenestepensjonSimuleringService(pidGetter, tjenestepensjonSimuleringClient, loependeVedtakService, ekskluderingFacade)
    }

    @Test
    fun `hent PID, map request, og hent simulering fra client`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1990-01-01"),
            uttaksdato = LocalDate.of(2053, 3, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = listOf(
                LoependeInntekt(
                    fom = LocalDate.of(2053, 3, 1),
                    beloep = 500000
                ),
                LoependeInntekt(
                    fom = LocalDate.of(2060, 3, 1),
                    beloep = 0
                )
            ),
            aarIUtlandetEtter16 = 6,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true,
            erApoteker = false
        )

        val start = Alder(62, 1)
        val slutt = Alder(63, 1)

        `when`(tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request, pid)).thenReturn(
            OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.OK),
                simuleringsResultat = SimuleringsResultat(
                    tpOrdning = "tpOrdning",
                    tpNummer = "111111",
                    perioder = listOf(Utbetaling(startAlder = start, sluttAlder = slutt, maanedligBeloep = 1000)),
                    betingetTjenestepensjonInkludert = true
                ),
                tpOrdninger = listOf("tpOrdning")
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertNotNull(result)
        assertEquals(ResultatType.OK, result.simuleringsResultatStatus.resultatType)
        assertNotNull(result.simuleringsResultat)

        val simuleringsResultat = result.simuleringsResultat!!
        assertEquals("tpOrdning", simuleringsResultat.tpOrdning)
        assertEquals("111111", simuleringsResultat.tpNummer)
        assertEquals(start, simuleringsResultat.perioder.get(0).startAlder)
        assertEquals(slutt, simuleringsResultat.perioder[0].sluttAlder)
        assertEquals(1000, simuleringsResultat.perioder[0].maanedligBeloep)
        assertTrue(simuleringsResultat.betingetTjenestepensjonInkludert)
        assertEquals("tpOrdning", result.tpOrdninger[0])
    }

    @Test
    fun `returnerer IKKE_MEDLEM status naar bruker har ufoeretrygd`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1962-12-31"),
            uttaksdato = LocalDate.of(2025, 1, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 0,
            brukerBaOmAfp = false,
            epsPensjon = false,
            eps2G = false,
            erApoteker = false
        )

        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = null,
                ufoeretrygd = LoependeUfoeretrygdDetaljer(grad = 100, fom = LocalDate.now()),
                afpPrivat = null,
                afpOffentlig = null,
                pre2025OffentligAfp = null
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertEquals(ResultatType.IKKE_MEDLEM, result.simuleringsResultatStatus.resultatType)
        assertNull(result.simuleringsResultat)
    }

    @Test
    fun `returnerer IKKE_MEDLEM status naar bruker har pre2025OffentligAfp`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1962-12-31"),
            uttaksdato = LocalDate.of(2025, 1, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 0,
            brukerBaOmAfp = false,
            epsPensjon = false,
            eps2G = false,
            erApoteker = false
        )

        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = null,
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
                pre2025OffentligAfp = LoependeVedtakDetaljer(fom = LocalDate.now())
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertEquals(ResultatType.IKKE_MEDLEM, result.simuleringsResultatStatus.resultatType)
        assertNull(result.simuleringsResultat)
    }

    @Test
    fun `returnerer IKKE_MEDLEM status naar bruker er apoteker`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1990-01-01"),
            uttaksdato = LocalDate.of(2053, 3, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 0,
            brukerBaOmAfp = false,
            epsPensjon = false,
            eps2G = false,
            erApoteker = false
        )

        `when`(ekskluderingFacade.apotekerEkskludering()).thenReturn(
            EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertEquals(ResultatType.IKKE_MEDLEM, result.simuleringsResultatStatus.resultatType)
        assertNull(result.simuleringsResultat)
    }

    @Test
    fun `returnerer IKKE_MEDLEM status naar bruker er foedt foer 1963 og har ufoeretrygd`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1962-12-31"),
            uttaksdato = LocalDate.of(2025, 1, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 0,
            brukerBaOmAfp = false,
            epsPensjon = false,
            eps2G = false,
            erApoteker = false
        )

        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = null,
                ufoeretrygd = LoependeUfoeretrygdDetaljer(grad = 100, fom = LocalDate.now()),
                afpPrivat = null,
                afpOffentlig = null,
                pre2025OffentligAfp = null
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertEquals(ResultatType.IKKE_MEDLEM, result.simuleringsResultatStatus.resultatType)
        assertNull(result.simuleringsResultat)
    }

    @Test
    fun `returnerer IKKE_MEDLEM status naar bruker er foedt foer 1963 og har pre2025OffentligAfp`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1962-12-31"),
            uttaksdato = LocalDate.of(2025, 1, 1),
            sisteInntekt = 500000,
            fremtidigeInntekter = emptyList(),
            aarIUtlandetEtter16 = 0,
            brukerBaOmAfp = false,
            epsPensjon = false,
            eps2G = false,
            erApoteker = false
        )

        `when`(loependeVedtakService.hentLoependeVedtak()).thenReturn(
            LoependeVedtak(
                alderspensjon = null,
                fremtidigLoependeVedtakAp = null,
                ufoeretrygd = null,
                afpPrivat = null,
                afpOffentlig = null,
                pre2025OffentligAfp = LoependeVedtakDetaljer(fom = LocalDate.now())
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertEquals(ResultatType.IKKE_MEDLEM, result.simuleringsResultatStatus.resultatType)
        assertNull(result.simuleringsResultat)
    }
}
