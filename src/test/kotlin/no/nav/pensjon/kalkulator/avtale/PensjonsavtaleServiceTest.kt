package no.nav.pensjon.kalkulator.avtale

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UttaksperiodeSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.Sivilstatus
import no.nav.pensjon.kalkulator.avtale.client.np.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtalerV3
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

    private lateinit var pensjonsavtaleService: PensjonsavtaleService

    @Mock
    private lateinit var pensjonsavtaleClient: PensjonsavtaleClient

    @Mock
    private lateinit var featureToggleService: FeatureToggleService

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        pensjonsavtaleService = PensjonsavtaleService(pensjonsavtaleClient, pidGetter, featureToggleService)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `fetchAvtaler fetches avtaler`() {
        arrangeClient()
        val result = pensjonsavtaleService.fetchAvtaler(pensjonsavtaleSpecDto())
        result shouldBe pensjonsavtaler
    }

    @Test
    fun `fetchAvtaler excludes avtaler of kategori 'Folketrygd' and 'Offentlig tjenestepensjon'`() {
        arrangeClient(
            listOf(
                AvtaleKategori.FOLKETRYGD,
                AvtaleKategori.PRIVAT_AFP,
                AvtaleKategori.OFFENTLIG_TJENESTEPENSJON
            )
        )

        val result = pensjonsavtaleService.fetchAvtaler(pensjonsavtaleSpecDto())

        result shouldBe pensjonsavtalerV3(listOf(AvtaleKategori.PRIVAT_AFP))
    }

    private fun arrangeClient() {
        `when`(pensjonsavtaleClient.fetchAvtaler(pensjonsavtaleSpec())).thenReturn(pensjonsavtaler)
    }

    private fun arrangeClient(kategorier: List<AvtaleKategori>) {
        `when`(pensjonsavtaleClient.fetchAvtaler(pensjonsavtaleSpec())).thenReturn(pensjonsavtalerV3(kategorier))
    }

    private companion object {
        private const val AARLIG_INNTEKT_FOER_UTTAK = 456000
        private const val ANTALL_INNTEKTSAAR_ETTER_UTTAK = 2
        private val pid = Pid("12906498357")
        private val pensjonsavtaler = pensjonsavtalerV3()

        private fun pensjonsavtaleSpecDto() =
            PensjonsavtaleSpecDto(
                AARLIG_INNTEKT_FOER_UTTAK,
                listOf(uttaksperiodeSpecDto()),
                ANTALL_INNTEKTSAAR_ETTER_UTTAK,
                harAfp = false,
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                antallAarIUtlandetEtter16 = 0,
                Sivilstatus.UGIFT,
                oenskesSimuleringAvFolketrygd = false
            )

        private fun pensjonsavtaleSpec() =
            PensjonsavtaleSpec(
                pid,
                AARLIG_INNTEKT_FOER_UTTAK,
                listOf(uttaksperiodeSpec()),
                ANTALL_INNTEKTSAAR_ETTER_UTTAK,
                harAfp = false,
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                antallAarIUtlandetEtter16 = 0,
                Sivilstatus.UGIFT,
                oenskesSimuleringAvFolketrygd = false
            )

        private fun uttaksperiodeSpec() = UttaksperiodeSpec(Alder(67, 1), Uttaksgrad.HUNDRE_PROSENT, 123000)

        private fun uttaksperiodeSpecDto() = UttaksperiodeSpecDto(67, 1, 100, 123000)
    }
}
