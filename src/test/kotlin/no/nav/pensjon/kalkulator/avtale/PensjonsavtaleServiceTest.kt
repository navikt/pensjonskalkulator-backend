package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtaler
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PensjonsavtaleServiceTest {

    private lateinit var service: PensjonsavtaleService

    @Mock
    private lateinit var pensjonsavtaleClient: PensjonsavtaleClient

    @Mock
    private lateinit var featureToggleService: FeatureToggleService

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = PensjonsavtaleService(pensjonsavtaleClient, pidGetter, featureToggleService)
    }

    @Test
    fun `fetchAvtaler fetches avtaler`() {
        arrangePidAndClient()

        val result = service.fetchAvtaler(pensjonsavtaleSpecDto())

        val avtale = result.avtaler[0]
        assertEquals("produkt1", avtale.produktbetegnelse)
        assertEquals("kategori1", avtale.kategori)
        assertEquals(67, avtale.startAlder)
        assertEquals(77, avtale.sluttAlder)
        val utbetalingsperiode = avtale.utbetalingsperioder[0]
        val start = utbetalingsperiode.start
        val slutt = utbetalingsperiode.slutt!!
        assertEquals(68, start.aar)
        assertEquals(1, start.maaned)
        assertEquals(78, slutt.aar)
        assertEquals(12, slutt.maaned)
        assertEquals(123000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
        val selskap = result.utilgjengeligeSelskap[0]
        assertEquals("selskap1", selskap.navn)
        assertTrue(selskap.heltUtilgjengelig)
    }

    private fun arrangePidAndClient() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(pensjonsavtaleClient.fetchAvtaler(pensjonsavtaleSpec())).thenReturn(pensjonsavtaler())
    }

    private companion object {
        private const val AARLIG_INNTEKT_FOER_UTTAK = 456000
        private const val ANTALL_INNTEKTSAAR_ETTER_UTTAK = 2
        private val pid = Pid("12906498357")
        private val uttaksperiodeSpec = listOf(uttaksperiodeSpec())

        private fun pensjonsavtaleSpecDto() =
            PensjonsavtaleSpecDto(
                AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperiodeSpec,
                ANTALL_INNTEKTSAAR_ETTER_UTTAK
            )

        private fun pensjonsavtaleSpec() =
            PensjonsavtaleSpec(pid, AARLIG_INNTEKT_FOER_UTTAK, uttaksperiodeSpec, ANTALL_INNTEKTSAAR_ETTER_UTTAK)

        private fun uttaksperiodeSpec() = UttaksperiodeSpec(67, 1, 100, 123000)
    }
}
