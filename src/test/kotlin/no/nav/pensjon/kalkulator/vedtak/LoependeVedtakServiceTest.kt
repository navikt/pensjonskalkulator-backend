package no.nav.pensjon.kalkulator.vedtak

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import java.time.LocalDate

class LoependeVedtakServiceTest : ShouldSpec({

    context("gjenlevenderett") {
        should("hente løpende vedtak og avdødes navn") {
            LoependeVedtakService(
                vedtakClient = arrangeVedtak,
                personService = arrangeAvdoed,
                pidGetter = mockk(relaxed = true)
            ).hentLoependeVedtak() shouldBe vedtakSamling(avdoedNavn)
        }
    }
})

private fun vedtakSamling(avdoedNavn: Navn?) =
    VedtakSamling(
        loependeAlderspensjon = LoependeAlderspensjon(
            grad = Uttaksgrad.TJUE_PROSENT,
            fom = LocalDate.of(2020, 10, 1),
            uttaksgradFom = LocalDate.of(2021, 1, 1),
            sivilstatus = Sivilstatus.UGIFT,
            harGjenlevenderett = true,
            harUtenlandsopphold = true
        ),
        fremtidigAlderspensjon = FremtidigAlderspensjon(
            grad = Uttaksgrad.FOERTI_PROSENT,
            fom = LocalDate.of(2023, 10, 1),
            sivilstatus = Sivilstatus.GIFT
        ),
        privatAfp = LoependeEntitet(fom = LocalDate.of(2022, 10, 1)),
        gjenlevenderett = Gjenlevenderett(
            avdoedPid = pid,
            doedsdato = LocalDate.of(2020, 1, 1),
            foersteVirkningsdato = LocalDate.of(2020, 1, 1),
            avdoedNavn = avdoedNavn
        ),
        ufoeretrygd = LoependeUfoeretrygd(grad = 2, fom = LocalDate.of(2021, 10, 1)),
        avdoed = InformasjonOmAvdoed(
            pid = pid,
            doedsdato = LocalDate.of(2025, 6, 14),
            foersteAlderspensjonVirkningsdato = LocalDate.of(2021, 1, 1),
            aarligPensjonsgivendeInntektErMinst1G = true,
            harTilstrekkeligMedlemskapIFolketrygden = false,
            antallAarUtenlands = 3,
            erFlyktning = true
        )
    )

private val avdoedNavn = Navn(fornavn = "F", etternavn = "E")

private val arrangeAvdoed: PersonService =
    mockk {
        every {
            getPerson(any())
        } returns Person(avdoedNavn, foedselsdato = LocalDate.of(1950, 10, 12))
    }

/**
 * Vedtaket inneholder ikke avdødes navn.
 */
private val arrangeVedtak: LoependeVedtakClient =
    mockk { every { hentLoependeVedtak(any()) } returns vedtakSamling(avdoedNavn = null) }