package no.nav.pensjon.kalkulator.avtale

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleIngressSpecDto
import no.nav.pensjon.kalkulator.avtale.api.dto.UttaksperiodeIngressSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtalerV3
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
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

        private val pensjonsavtaler = pensjonsavtalerV3()

        private fun pensjonsavtaleSpecDto() =
            PensjonsavtaleIngressSpecDto(
                aarligInntektFoerUttak = AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperioder = listOf(uttaksperiodeSpecDto()),
                antallInntektsaarEtterUttak = ANTALL_INNTEKTSAAR_ETTER_UTTAK,
                harAfp = false,
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                antallAarIUtlandetEtter16 = 0,
                sivilstatus = Sivilstand.UGIFT,
                oenskesSimuleringAvFolketrygd = false
            )

        private fun pensjonsavtaleSpec() =
            PensjonsavtaleSpec(
                pid = pid,
                aarligInntektFoerUttak = AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperioder = listOf(uttaksperiodeSpec()),
                antallInntektsaarEtterUttak = ANTALL_INNTEKTSAAR_ETTER_UTTAK,
                harAfp = false,
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                antallAarIUtlandetEtter16 = 0,
                sivilstatus = Sivilstand.UGIFT,
                oenskesSimuleringAvFolketrygd = false
            )

        private fun uttaksperiodeSpec() =
            UttaksperiodeSpec(
                start = Alder(67, 1),
                grad = Uttaksgrad.HUNDRE_PROSENT,
                aarligInntekt = 123000
            )

        private fun uttaksperiodeSpecDto() =
            UttaksperiodeIngressSpecDto(
                startAlder = 67,
                startMaaned = 2, // DTO startMaaned = alder-maaneder + 1
                grad = 100,
                aarligInntekt = 123000
            )
    }
}
