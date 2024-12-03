package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.*
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

    @BeforeEach
    fun initialize() {
        `when`(pidGetter.pid()).thenReturn(pid)
        service = TjenestepensjonSimuleringService(pidGetter, tjenestepensjonSimuleringClient)
    }

    @Test
    fun `hent pid, map request og hent simulering fra client`() {
        val request = SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = LocalDate.parse("1990-01-01"),
            uttaksdato = LocalDate.of(2053, 3, 1),
            sisteInntekt = 500000,
            aarIUtlandetEtter16 = 6,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true
        )

        val start = Alder(62,1)
        val slutt = Alder(63, 1)

        `when`(tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request, pid)).thenReturn(
            OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.OK),
                simuleringsResultat = SimuleringsResultat(
                    tpOrdning = "tpOrdning",
                    perioder = listOf(Utbetaling(startAlder = start, sluttAlder = slutt, maanedligBeloep = 1000)),
                    betingetTjenestepensjonInkludert = true
                ),
                tpOrdninger = listOf("tpOrdning")
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertNotNull(result)
        assertEquals(ResultatType.OK, result.simuleringsResultatStatus.resultatType)
        assertEquals("tpOrdning", result.simuleringsResultat!!.tpOrdning)
        assertEquals(start, result.simuleringsResultat.perioder[0].startAlder)
        assertEquals(slutt, result.simuleringsResultat.perioder[0].sluttAlder)
        assertEquals(1000, result.simuleringsResultat.perioder[0].maanedligBeloep)
        assertTrue(result.simuleringsResultat.betingetTjenestepensjonInkludert)
        assertEquals("tpOrdning", result.tpOrdninger[0])
    }
}