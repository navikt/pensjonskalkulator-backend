package no.nav.pensjon.kalkulator.avtale

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.mock.NorskPensjonMockPensjonsavtaleClient
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.avtaleMedToUtbetalingsperioder
import no.nav.pensjon.kalkulator.mock.PensjonsavtaleFactory.pensjonsavtalerV3
import no.nav.pensjon.kalkulator.mock.PersonFactory
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.tech.env.EnvironmentUtil
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService

class PensjonsavtaleServiceTest : ShouldSpec({

    beforeSpec {
        mockkObject(EnvironmentUtil)
    }

    beforeTest {
        every { EnvironmentUtil.isDevelopment() } returns false
    }

    afterSpec {
        unmockkObject(EnvironmentUtil)
    }

    should("fetch avtaler") {
        val avtaleService = arrangeService(client = arrangeAvtaler(pensjonsavtalerV3()))

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe pensjonsavtalerV3()
    }

    should("fetch avtaler fra mock-klient når toggle er aktivert i development") {
        val spec = avtaleSpecMedLivsvarigInntekt()
        val forventedeAvtaler = pensjonsavtalerV3()
        val client = mockk<PensjonsavtaleClient>()
        val mockClient = mockk<NorskPensjonMockPensjonsavtaleClient> {
            every { hent(spec) } returns forventedeAvtaler
        }
        val featureToggleService = mockk<FeatureToggleService> {
            every { isEnabled("mock-norsk-pensjon") } returns true
        }
        val avtaleService = arrangeService(client, mockClient, featureToggleService)

        every { EnvironmentUtil.isDevelopment() } returns true

        avtaleService.fetchAvtaler(spec) shouldBe forventedeAvtaler

        verify(exactly = 1) { featureToggleService.isEnabled("mock-norsk-pensjon") }
        verify(exactly = 1) { mockClient.hent(spec) }
        verify { client wasNot Called }
    }

    should("fetch avtaler fra ordinær klient når toggle er deaktivert i development") {
        val spec = avtaleSpecMedLivsvarigInntekt()
        val forventedeAvtaler = pensjonsavtalerV3()
        val client = mockk<PensjonsavtaleClient> {
            every { fetchAvtaler(spec) } returns forventedeAvtaler
        }
        val mockClient = mockk<NorskPensjonMockPensjonsavtaleClient>()
        val featureToggleService = mockk<FeatureToggleService> {
            every { isEnabled("mock-norsk-pensjon") } returns false
        }
        val avtaleService = arrangeService(client, mockClient, featureToggleService)

        every { EnvironmentUtil.isDevelopment() } returns true

        avtaleService.fetchAvtaler(spec) shouldBe forventedeAvtaler

        verify(exactly = 1) { featureToggleService.isEnabled("mock-norsk-pensjon") }
        verify(exactly = 1) { client.fetchAvtaler(spec) }
        verify { mockClient wasNot Called }
    }

    should("fetch avtaler fra ordinær klient uten å evaluere toggle i production") {
        val spec = avtaleSpecMedLivsvarigInntekt()
        val forventedeAvtaler = pensjonsavtalerV3()
        val client = mockk<PensjonsavtaleClient> {
            every { fetchAvtaler(spec) } returns forventedeAvtaler
        }
        val mockClient = mockk<NorskPensjonMockPensjonsavtaleClient>()
        val featureToggleService = mockk<FeatureToggleService>()
        val avtaleService = arrangeService(client, mockClient, featureToggleService)

        avtaleService.fetchAvtaler(spec) shouldBe forventedeAvtaler

        verify(exactly = 1) { client.fetchAvtaler(spec) }
        verify { featureToggleService wasNot Called }
        verify { mockClient wasNot Called }
    }

    should("exclude avtaler of kategori 'Folketrygd', 'Offentlig tjenestepensjon', 'Privat AFP'") {
        val avtaleService = arrangeService(
            client = arrangeAvtaler(
                kategorier = listOf(
                    AvtaleKategori.FOLKETRYGD,
                    AvtaleKategori.OFFENTLIG_TJENESTEPENSJON,
                    AvtaleKategori.INDIVIDUELL_ORDNING,
                    AvtaleKategori.PRIVAT_AFP
                )
            )
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                pensjonsavtalerV3(kategorier = listOf(AvtaleKategori.INDIVIDUELL_ORDNING))
    }

    should("exclude avtaler uten startår") {
        val avtaleService = arrangeService(client = arrangeAvtaler(enAvtaleUtenStart()))

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                Pensjonsavtaler(avtaler = emptyList(), utilgjengeligeSelskap = emptyList())
    }

    should("fjerne avtaler og legge til selskap som utilgjengelig for alle kjente årsaker til manglende beregning") {
        val eksisterendeUtilgjengeligSelskap = Selskap("Eksisterende selskap", true)
        val utilgjengeligeAvtaler = ManglendeEksternBeregningAarsak.entries
            .filterNot { it in listOf(ManglendeEksternBeregningAarsak.NONE, ManglendeEksternBeregningAarsak.UNKNOWN) }
            .mapIndexed { index, aarsak -> avtaleUtenUtbetalingsperioder("Selskap $index", aarsak) }
        val avtaleService = arrangeService(
            client = arrangeAvtaler(
                Pensjonsavtaler(
                    avtaler = utilgjengeligeAvtaler + avtaleMedToUtbetalingsperioder,
                    utilgjengeligeSelskap = listOf(eksisterendeUtilgjengeligSelskap)
                )
            )
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                Pensjonsavtaler(
                    avtaler = listOf(avtaleMedToUtbetalingsperioder),
                    utilgjengeligeSelskap = listOf(eksisterendeUtilgjengeligSelskap) +
                            utilgjengeligeAvtaler.map { Selskap(it.selskapsnavn, true) }
                )
    }

    should("ikke legge til selskap som utilgjengelig når manglende beregningsårsak er none eller unknown") {
        val avtaler = listOf(
            avtaleUtenUtbetalingsperioder("Selskap uten årsak", ManglendeEksternBeregningAarsak.NONE),
            avtaleUtenUtbetalingsperioder("Selskap med ukjent årsak", ManglendeEksternBeregningAarsak.UNKNOWN)
        )
        val avtaleService = arrangeService(client = arrangeAvtaler(Pensjonsavtaler(avtaler, emptyList())))

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                Pensjonsavtaler(avtaler = avtaler, utilgjengeligeSelskap = emptyList())
    }

    should("ikke legge til selskap som utilgjengelig når avtalen har utbetalingsperioder") {
        val avtaler = listOf(avtaleMedToUtbetalingsperioder)
        val avtaleService = arrangeService(client = arrangeAvtaler(Pensjonsavtaler(avtaler, emptyList())))

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                Pensjonsavtaler(avtaler = avtaler, utilgjengeligeSelskap = emptyList())
    }
})

private fun arrangeService(
    client: PensjonsavtaleClient,
    mockClient: NorskPensjonMockPensjonsavtaleClient = mockk(),
    featureToggleService: FeatureToggleService = mockk()
) =
    PensjonsavtaleService(
        client = client,
        mockClient = mockClient,
        featureToggleService = featureToggleService
    )

private fun arrangeAvtaler(avtaler: Pensjonsavtaler): PensjonsavtaleClient =
    mockk<PensjonsavtaleClient>().apply {
        every { fetchAvtaler(any()) } returns avtaler
    }

private fun arrangeAvtaler(kategorier: List<AvtaleKategori>): PensjonsavtaleClient =
    mockk<PensjonsavtaleClient>().apply {
        every { fetchAvtaler(any()) } returns pensjonsavtalerV3(kategorier)
    }

private fun avtaleSpecMedLivsvarigInntekt() =
    PensjonsavtaleSpec(
        person = PersonSpec(pid = PersonFactory.pid, foedselsdato = PersonFactory.foedselsdato),
        aarligInntektFoerUttak = 456000,
        uttaksperioder = listOf(gradertUttak(), heltUttak()),
        harEpsPensjon = true,
        harEpsPensjonsgivendeInntektOver2G = true,
        sivilstatus = Sivilstatus.UGIFT
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

private fun avtaleUtenUtbetalingsperioder(
    selskapsnavn: String,
    manglendeBeregningAarsak: ManglendeEksternBeregningAarsak
) =
    avtaleMedToUtbetalingsperioder.copy(
        selskapsnavn = selskapsnavn,
        manglendeBeregningAarsak = manglendeBeregningAarsak,
        utbetalingsperioder = emptyList()
    )

