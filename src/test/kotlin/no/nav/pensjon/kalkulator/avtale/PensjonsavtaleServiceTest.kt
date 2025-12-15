package no.nav.pensjon.kalkulator.avtale

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtalerV3
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService

class PensjonsavtaleServiceTest : ShouldSpec({

    should("fetch avtaler") {
        val avtaleService = PensjonsavtaleService(
            avtaleClient = arrangeAvtaler(pensjonsavtalerV3()),
            mockAvtaleClient = mockk(),
            pidGetter = mockk(relaxed = true),
            featureToggleService = mockk(relaxed = true)
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe pensjonsavtalerV3()
    }

    should("exclude avtaler of kategori 'Folketrygd', 'Offentlig tjenestepensjon', 'Privat AFP'") {
        val avtaleService = PensjonsavtaleService(
            avtaleClient = arrangeAvtaler(
                kategorier = listOf(
                    AvtaleKategori.FOLKETRYGD,
                    AvtaleKategori.OFFENTLIG_TJENESTEPENSJON,
                    AvtaleKategori.INDIVIDUELL_ORDNING,
                    AvtaleKategori.PRIVAT_AFP
                )
            ),
            mockAvtaleClient = mockk(),
            pidGetter = mockk(relaxed = true),
            featureToggleService = mockk(relaxed = true)
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                pensjonsavtalerV3(kategorier = listOf(AvtaleKategori.INDIVIDUELL_ORDNING))
    }

    should("exclude avtaler uten startår") {
        val avtaleService = PensjonsavtaleService(
            avtaleClient = arrangeAvtaler(enAvtaleUtenStart()),
            mockAvtaleClient = mockk(),
            pidGetter = mockk(relaxed = true),
            featureToggleService = mockk(relaxed = true)
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                Pensjonsavtaler(avtaler = emptyList(), utilgjengeligeSelskap = emptyList())
    }

    should("use mocked avtaler when 'mock' feature is enabled") {
        val spec = avtaleSpecMedLivsvarigInntekt()
        val realAvtaleClient = mockk<PensjonsavtaleClient>()
        val mockAvtaleClient = arrangeAvtaler(pensjonsavtalerV3())
        val avtaleService = PensjonsavtaleService(
            realAvtaleClient,
            mockAvtaleClient,
            pidGetter = mockk(relaxed = true),
            featureToggleService = arrangeFeature(enabled = true)
        )

        avtaleService.fetchAvtaler(spec)

        verify(exactly = 1) { mockAvtaleClient.fetchAvtaler(spec = eq(spec), pid = any()) }
        verify { realAvtaleClient wasNot Called }
    }

    should("use real avtale-service when 'mock' feature is disabled") {
        val spec = avtaleSpecMedLivsvarigInntekt()
        val realAvtaleClient = arrangeAvtaler(pensjonsavtalerV3())
        val mockAvtaleClient = mockk<PensjonsavtaleClient>()
        val avtaleService = PensjonsavtaleService(
            realAvtaleClient,
            mockAvtaleClient,
            pidGetter = mockk(relaxed = true),
            featureToggleService = arrangeFeature(enabled = false)
        )

        avtaleService.fetchAvtaler(spec)

        verify(exactly = 1) { realAvtaleClient.fetchAvtaler(spec = eq(spec), pid = any()) }
        verify { mockAvtaleClient wasNot Called }
    }
})

private fun arrangeAvtaler(avtaler: Pensjonsavtaler): PensjonsavtaleClient =
    mockk<PensjonsavtaleClient>().apply {
        every { fetchAvtaler(any(), any()) } returns avtaler
    }

private fun arrangeAvtaler(kategorier: List<AvtaleKategori>): PensjonsavtaleClient =
    mockk<PensjonsavtaleClient>().apply {
        every { fetchAvtaler(any(), any()) } returns pensjonsavtalerV3(kategorier)
    }

private fun arrangeFeature(enabled: Boolean): FeatureToggleService =
    mockk<FeatureToggleService>().apply {
        every { isEnabled(featureName = "mock-norsk-pensjon") } returns enabled
    }

private fun avtaleSpecMedLivsvarigInntekt() =
    PensjonsavtaleSpec(
        aarligInntektFoerUttak = 456000,
        uttaksperioder = listOf(gradertUttak(), heltUttak()),
        harEpsPensjon = true,
        harEpsPensjonsgivendeInntektOver2G = true,
        sivilstand = Sivilstand.UGIFT
    )

private fun gradertUttak() =
    UttaksperiodeSpec(
        startAlder = Alder(aar = 67, maaneder = 1),
        grad = Uttaksgrad.AATTI_PROSENT,
        aarligInntekt = InntektSpec(aarligBeloep = 123000, tomAlder = null)
    )

private fun heltUttak() =
    UttaksperiodeSpec(
        startAlder = Alder(aar = 70, maaneder = 1),
        grad = Uttaksgrad.HUNDRE_PROSENT,
        aarligInntekt = InntektSpec(aarligBeloep = 45000, tomAlder = null)
    )

private fun enAvtaleUtenStart() =
    Pensjonsavtaler(
        avtaler = listOf(avtaleUtenStart()),
        utilgjengeligeSelskap = emptyList()
    )

private fun avtaleUtenStart() =
    Pensjonsavtale(
        produktbetegnelse = "produkt1",
        kategori = AvtaleKategori.INDIVIDUELL_ORDNING,
        startalder = 0,
        sluttalder = null,
        utbetalingsperioder = emptyList()
    )
