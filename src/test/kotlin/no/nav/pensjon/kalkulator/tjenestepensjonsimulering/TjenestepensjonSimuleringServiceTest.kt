package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.SimuleringOFTPSpec
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
        val request = IngressSimuleringOFTPSpecV2(
            foedselsdato = LocalDate.parse("1990-01-01"),
            uttaksalder = Alder(aar = 63, maaneder = 1),
            aarligInntektFoerUttakBeloep = 500000,
            antallAarIUtlandetEtter16 = 6,
            epsHarPensjon = true,
            epsHarInntektOver2G = true,
            brukerBaOmAfp = true
        )

        val mappedRequest = SimuleringOFTPSpec(
            pid = pid.value,
            foedselsdato = request.foedselsdato,
            uttaksdato = LocalDate.of(2053, 3, 1),
            sisteInntekt = 500000,
            aarIUtlandetEtter16 = 6,
            brukerBaOmAfp = true,
            epsPensjon = true,
            eps2G = true
        )

        `when`(tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(mappedRequest)).thenReturn(
            OFTPSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(resultatType = ResultatType.OK),
                simuleringsResultat = SimuleringsResultat(
                    tpOrdning = "tpOrdning",
                    perioder = listOf(Utbetaling(aar = 2021, beloep = 1000)),
                    btpInkludert = true
                ),
                tpOrdninger = listOf("tpOrdning")
            )
        )

        val result = service.hentTjenestepensjonSimulering(request)

        assertNotNull(result)
        assertEquals(ResultatType.OK, result.simuleringsResultatStatus.resultatType)
        assertEquals("tpOrdning", result.simuleringsResultat!!.tpOrdning)
        assertEquals(2021, result.simuleringsResultat.perioder[0].aar)
        assertEquals(1000, result.simuleringsResultat.perioder[0].beloep)
        assertTrue(result.simuleringsResultat.btpInkludert)
        assertEquals("tpOrdning", result.tpOrdninger[0])
    }
}