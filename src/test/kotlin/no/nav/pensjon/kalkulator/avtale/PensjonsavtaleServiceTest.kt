package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
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
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = PensjonsavtaleService(pensjonsavtaleClient, pidGetter)
    }

    @Test
    fun `fetchAvtaler fetches avtaler`() {
        arrangePidAndClient()

        val avtale = service.fetchAvtaler(pensjonsavtaleSpecDto())

        assertEquals("produkt1", avtale.produktbetegnelse)
        assertEquals("kategori1", avtale.kategori)
        assertEquals(67, avtale.startAlder)
        assertEquals(77, avtale.sluttAlder)
        val utbetalingsperiode = avtale.utbetalingsperiode
        val start = utbetalingsperiode.start
        val slutt = utbetalingsperiode.slutt!!
        assertEquals(68, start.aar)
        assertEquals(1, start.maaned)
        assertEquals(76, slutt.aar)
        assertEquals(12, slutt.maaned)
        assertEquals(12000, utbetalingsperiode.aarligUtbetaling)
        assertEquals(100, utbetalingsperiode.grad)
    }

    private fun arrangePidAndClient() {
        `when`(pidGetter.pid()).thenReturn(pid)
        `when`(pensjonsavtaleClient.fetchAvtaler(pensjonsavtaleSpec())).thenReturn(pensjonsavtale())
    }


    private companion object {

        private const val AARLIG_INNTEKT_FOER_UTTAK = 456000
        private const val ANTALL_INNTEKTSAAR_ETTER_UTTAK = 2
        private val pid = Pid("12906498357")
        private val uttaksperiodeSpec = uttaksperiodeSpec()

        private fun pensjonsavtaleSpecDto() =
            PensjonsavtaleSpecDto(
                AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperiodeSpec,
                ANTALL_INNTEKTSAAR_ETTER_UTTAK
            )

        private fun pensjonsavtaleSpec() =
            PensjonsavtaleSpec(pid, AARLIG_INNTEKT_FOER_UTTAK, uttaksperiodeSpec, ANTALL_INNTEKTSAAR_ETTER_UTTAK)

        private fun uttaksperiodeSpec() = UttaksperiodeSpec(67, 1, 100, 123000)

        private fun pensjonsavtale() =
            Pensjonsavtale(
                "produkt1",
                "kategori1",
                67,
                77,
                utbetalingsperiode()
            )

        private fun utbetalingsperiode() =
            Utbetalingsperiode(
                Alder(68, 1),
                Alder(76, 12),
                12000,
                100
            )
    }
}
