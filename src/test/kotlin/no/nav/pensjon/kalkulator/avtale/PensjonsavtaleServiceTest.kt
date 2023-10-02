package no.nav.pensjon.kalkulator.avtale

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
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

    private lateinit var avtaleService: PensjonsavtaleService

    @Mock
    private lateinit var avtaleClient: PensjonsavtaleClient

    @Mock
    private lateinit var featureToggleService: FeatureToggleService

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        avtaleService = PensjonsavtaleService(avtaleClient, pidGetter, featureToggleService)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `fetchAvtaler fetches avtaler`() {
        arrangeClient()
        val result = avtaleService.fetchAvtaler(pensjonsavtaleSpec())
        result shouldBe pensjonsavtaler
    }

    @Test
    fun `fetchAvtaler excludes avtaler of kategori 'Folketrygd', 'Offentlig tjenestepensjon', 'Privat AFP'`() {
        arrangeClient(
            listOf(
                AvtaleKategori.FOLKETRYGD,
                AvtaleKategori.OFFENTLIG_TJENESTEPENSJON,
                AvtaleKategori.INDIVIDUELL_ORDNING,
                AvtaleKategori.PRIVAT_AFP
            )
        )

        val result = avtaleService.fetchAvtaler(pensjonsavtaleSpec())

        result shouldBe pensjonsavtalerV3(listOf(AvtaleKategori.INDIVIDUELL_ORDNING))
    }

    private fun arrangeClient() {
        `when`(avtaleClient.fetchAvtaler(pensjonsavtaleSpec(), pid)).thenReturn(pensjonsavtaler)
    }

    private fun arrangeClient(kategorier: List<AvtaleKategori>) {
        `when`(avtaleClient.fetchAvtaler(pensjonsavtaleSpec(), pid)).thenReturn(pensjonsavtalerV3(kategorier))
    }

    companion object {
        private const val AARLIG_INNTEKT_FOER_UTTAK = 456000
        private const val ANTALL_INNTEKTSAAR_ETTER_UTTAK = 2

        private val pensjonsavtaler = pensjonsavtalerV3()

        fun pensjonsavtaleSpec() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperioder = listOf(uttaksperiodeSpec1(), uttaksperiodeSpec2()),
                antallInntektsaarEtterUttak = ANTALL_INNTEKTSAAR_ETTER_UTTAK,
                harAfp = false,
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                antallAarIUtlandetEtter16 = 0,
                sivilstand = Sivilstand.UGIFT
            )

        private fun uttaksperiodeSpec1() =
            UttaksperiodeSpec(
                startAlder = Alder(67, 1),
                grad = Uttaksgrad.AATTI_PROSENT,
                aarligInntekt = 123000
            )

        private fun uttaksperiodeSpec2() =
            UttaksperiodeSpec(
                startAlder = Alder(70, 1),
                grad = Uttaksgrad.HUNDRE_PROSENT,
                aarligInntekt = 45000
            )
    }
}
