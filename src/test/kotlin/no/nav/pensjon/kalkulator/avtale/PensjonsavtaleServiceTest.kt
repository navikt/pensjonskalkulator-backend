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
    private lateinit var mockAvtaleClient: PensjonsavtaleClient

    @Mock
    private lateinit var featureToggleService: FeatureToggleService

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        avtaleService = PensjonsavtaleService(avtaleClient, mockAvtaleClient, pidGetter, featureToggleService)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `fetchAvtaler fetches avtaler`() {
        arrangeClient()
        val result = avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt())
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

        val result = avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt())

        result shouldBe pensjonsavtalerV3(listOf(AvtaleKategori.INDIVIDUELL_ORDNING))
    }

    @Test
    fun `fetchAvtaler excludes avtaler without start-aar`() {
        `when`(avtaleClient.fetchAvtaler(avtaleSpecMedLivsvarigInntekt(), pid)).thenReturn(enAvtaleUtenStart())
        val result = avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt())
        result shouldBe Pensjonsavtaler(emptyList(), emptyList())
    }

    @Test
    fun `fetchAvtaler uses mocked avtaler, when toggle is set`() {
        val spec = avtaleSpecMedLivsvarigInntekt()
        `when`(featureToggleService.isEnabled("mock-norsk-pensjon")).thenReturn(true)
        `when`(mockAvtaleClient.fetchAvtaler(spec, pid)).thenReturn(pensjonsavtalerV3())

        avtaleService.fetchAvtaler(spec)

        verify(mockAvtaleClient, atLeastOnce()).fetchAvtaler(spec, pid)
        verify(avtaleClient, never()).fetchAvtaler(spec, pid)
    }

    @Test
    fun `fetchAvtaler uses norsk pensjon, when toggle is not set`() {
        val spec = avtaleSpecMedLivsvarigInntekt()
        `when`(featureToggleService.isEnabled("mock-norsk-pensjon")).thenReturn(false)
        `when`(avtaleClient.fetchAvtaler(spec, pid)).thenReturn(pensjonsavtalerV3())

        avtaleService.fetchAvtaler(spec)

        verify(avtaleClient, atLeastOnce()).fetchAvtaler(spec, pid)
        verify(mockAvtaleClient, never()).fetchAvtaler(spec, pid)
    }

    private fun arrangeClient() {
        `when`(avtaleClient.fetchAvtaler(avtaleSpecMedLivsvarigInntekt(), pid)).thenReturn(pensjonsavtaler)
    }

    private fun arrangeClient(kategorier: List<AvtaleKategori>) {
        `when`(avtaleClient.fetchAvtaler(avtaleSpecMedLivsvarigInntekt(), pid))
            .thenReturn(pensjonsavtalerV3(kategorier))
    }

    companion object {
        private const val AARLIG_INNTEKT_FOER_UTTAK = 456000

        private val pensjonsavtaler = pensjonsavtalerV3()

        fun avtaleSpecMedLivsvarigInntekt() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperioder = listOf(
                    uttaksperiodeSpecForGradertUttak(inntektTom = null),
                    uttaksperiodeSpecForHeltUttak(inntektTom = null)
                ),
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                sivilstand = Sivilstand.UGIFT
            )

        fun avtaleSpecMedTidsbegrensetInntekt() =
            PensjonsavtaleSpec(
                aarligInntektFoerUttak = AARLIG_INNTEKT_FOER_UTTAK,
                uttaksperioder = listOf(
                    uttaksperiodeSpecForGradertUttak(inntektTom = Alder(aar = 67, maaneder = 1)),
                    uttaksperiodeSpecForHeltUttak(inntektTom = Alder(aar = 69, maaneder = 1))
                ),
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                sivilstand = Sivilstand.UGIFT
            )

        private fun uttaksperiodeSpecForGradertUttak(inntektTom: Alder?) =
            UttaksperiodeSpec(
                startAlder = Alder(67, 1),
                grad = Uttaksgrad.AATTI_PROSENT,
                aarligInntekt = InntektSpec(
                    aarligBeloep = 123000,
                    tomAlder = inntektTom
                )
            )

        private fun uttaksperiodeSpecForHeltUttak(inntektTom: Alder?) =
            UttaksperiodeSpec(
                startAlder = Alder(70, 1),
                grad = Uttaksgrad.HUNDRE_PROSENT,
                aarligInntekt = InntektSpec(
                    aarligBeloep = 45000,
                    tomAlder = inntektTom
                )
            )

        private fun enAvtaleUtenStart() = Pensjonsavtaler(listOf(avtaleUtenStart()), emptyList())

        private fun avtaleUtenStart() =
            Pensjonsavtale(
                produktbetegnelse = "produkt1",
                kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
                startalder = 0,
                sluttalder = null,
                utbetalingsperioder = emptyList()
            )
    }
}
