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
import no.nav.pensjon.kalkulator.mock.PersonFactory
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService

class PensjonsavtaleServiceTest : ShouldSpec({

    should("fetch avtaler") {
        val avtaleService = PensjonsavtaleService(
            client = arrangeAvtaler(pensjonsavtalerV3()),
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe pensjonsavtalerV3()
    }

    should("exclude avtaler of kategori 'Folketrygd', 'Offentlig tjenestepensjon', 'Privat AFP'") {
        val avtaleService = PensjonsavtaleService(
            client = arrangeAvtaler(
                kategorier = listOf(
                    AvtaleKategori.FOLKETRYGD,
                    AvtaleKategori.OFFENTLIG_TJENESTEPENSJON,
                    AvtaleKategori.INDIVIDUELL_ORDNING,
                    AvtaleKategori.PRIVAT_AFP
                )
            ),
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                pensjonsavtalerV3(kategorier = listOf(AvtaleKategori.INDIVIDUELL_ORDNING))
    }

    should("exclude avtaler uten startår") {
        val avtaleService = PensjonsavtaleService(
            client = arrangeAvtaler(enAvtaleUtenStart()),
        )

        avtaleService.fetchAvtaler(avtaleSpecMedLivsvarigInntekt()) shouldBe
                Pensjonsavtaler(avtaler = emptyList(), utilgjengeligeSelskap = emptyList())
    }
})

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
