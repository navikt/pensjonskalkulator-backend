package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.EpsSpec
import no.nav.pensjon.kalkulator.simulering.LevendeEps
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjonsforhold
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClient
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringFoer1963Client
import no.nav.pensjon.kalkulator.vedtak.LoependeEntitet
import no.nav.pensjon.kalkulator.vedtak.LoependeUfoeretrygd
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling
import java.time.LocalDate

class TjenestepensjonSimuleringFoer1963ServiceTest : ShouldSpec({

    should("feilmelde med 'tjenestepensjonsordning støttes ikke' når personen er apoteker") {
        val spec = createSpec(foedselsdato = LocalDate.of(1964, 5, 15))
        val pid = Pid("12345678901")
        val tpOrdninger = listOf("TP001", "TP002")

        val tpClient = arrangeTpClient(pid, tpOrdninger)
        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = mockk(),
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = false,
                harTidsbegrensetOffentligAfp = false
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = true),
            tpclient = tpClient
        )

        val resultat = service.hentTjenestepensjonSimulering(spec)

        with(resultat) {
            feilkode shouldBe Feilkode.TP_ORDNING_STOETTES_IKKE
            relevanteTpOrdninger shouldBe tpOrdninger
            tpnr shouldBe null
            navnOrdning shouldBe null
            utbetalingsperioder shouldBe emptyList()
        }

        verify { tpClient.tjenestepensjonsforhold(pid) }
    }

    should("feilmelde med 'tjenestepensjonsordning støttes ikke' når personen er født før 1963 og har uføretrygd") {
        val spec = createSpec(foedselsdato = LocalDate.of(1962, 8, 20))
        val pid = Pid("12345678902")
        val tpOrdninger = listOf("TP003")

        val tpClient = arrangeTpClient(pid, tpOrdninger)
        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = mockk(),
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = true,
                harTidsbegrensetOffentligAfp = false
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = false),
            tpclient = tpClient
        )

        val resultat = service.hentTjenestepensjonSimulering(spec)

        with(resultat) {
            feilkode shouldBe Feilkode.TP_ORDNING_STOETTES_IKKE
            relevanteTpOrdninger shouldBe tpOrdninger
        }

        verify { tpClient.tjenestepensjonsforhold(pid) }
    }

    should("feilmelde med 'tjenestepensjonsordning støttes ikke' når personen er født før 1963 og har tidsbegrenset offentlig AFP") {
        val spec = createSpec(foedselsdato = LocalDate.of(1960, 3, 10))
        val pid = Pid("12345678903")
        val tpOrdninger = listOf("TP004", "TP005")

        val tpClient = arrangeTpClient(pid, tpOrdninger)
        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = mockk(),
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = false,
                harTidsbegrensetOffentligAfp = true
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = false),
            tpclient = tpClient
        )

        val resultat = service.hentTjenestepensjonSimulering(spec)

        with(resultat) {
            feilkode shouldBe Feilkode.TP_ORDNING_STOETTES_IKKE
            relevanteTpOrdninger shouldBe tpOrdninger
        }

        verify { tpClient.tjenestepensjonsforhold(pid) }
    }

    should("feilmelde med 'tjenestepensjonsordning støttes ikke' når personen er født før 1963 og har både uføretrygd og tidsbegrenset offentlig AFP") {
        val spec = createSpec(foedselsdato = LocalDate.of(1961, 11, 25))
        val pid = Pid("12345678904")
        val tpOrdninger = listOf("TP006")

        val tpClient = arrangeTpClient(pid, tpOrdninger)
        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = mockk(),
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = true,
                harTidsbegrensetOffentligAfp = true
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = false),
            tpclient = tpClient
        )

        val resultat = service.hentTjenestepensjonSimulering(spec)

        with(resultat) {
            feilkode shouldBe Feilkode.TP_ORDNING_STOETTES_IKKE
            relevanteTpOrdninger shouldBe tpOrdninger
        }

        verify { tpClient.tjenestepensjonsforhold(pid) }
    }

    should("feilmelde med 'tjenestepensjonsordning støttes ikke' når personen er både apoteker og født før 1963 med uføretrygd") {
        val spec = createSpec(foedselsdato = LocalDate.of(1962, 6, 15))
        val pid = Pid("12345678905")
        val tpOrdninger = listOf("TP007", "TP008", "TP009")

        val tpClient = arrangeTpClient(pid, tpOrdninger)
        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = mockk(),
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = true,
                harTidsbegrensetOffentligAfp = false
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = true),
            tpclient = tpClient
        )

        val resultat = service.hentTjenestepensjonSimulering(spec)

        with(resultat) {
            feilkode shouldBe Feilkode.TP_ORDNING_STOETTES_IKKE
            relevanteTpOrdninger shouldBe tpOrdninger
        }

        verify { tpClient.tjenestepensjonsforhold(pid) }
    }

    should("gjøre kall til tjenestepensjonSimuleringClient når ingen ekskluderingsårsaker foreligger") {
        val spec = createSpec(foedselsdato = LocalDate.of(1964, 5, 15))
        val pid = Pid("12345678906")
        val expectedResult = OffentligTjenestepensjonSimuleringFoer1963Resultat(
            tpnr = "TP010",
            navnOrdning = "Test Ordning",
            utbetalingsperioder = emptyList(),
            feilkode = null,
            relevanteTpOrdninger = emptyList()
        )

        val tjenestepensjonSimuleringClient = mockk<TjenestepensjonSimuleringFoer1963Client>()
        every {
            tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(spec, pid)
        } returns expectedResult

        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = tjenestepensjonSimuleringClient,
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = false,
                harTidsbegrensetOffentligAfp = false
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = false),
            tpclient = mockk()
        )

        service.hentTjenestepensjonSimulering(spec) shouldBe expectedResult

        verify { tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(spec, pid) }
    }

    should("gjøre kall til tjenestepensjonSimuleringClient når bruker er født før 1963 men ikke har uføretrygd eller tidsbegrenset offentlig AFP") {
        val spec = createSpec(foedselsdato = LocalDate.of(1960, 12, 1))
        val pid = Pid("12345678907")
        val expectedResult = OffentligTjenestepensjonSimuleringFoer1963Resultat(
            tpnr = "TP011",
            navnOrdning = "Another Ordning"
        )

        val tjenestepensjonSimuleringClient = mockk<TjenestepensjonSimuleringFoer1963Client>()
        every {
            tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(spec, pid)
        } returns expectedResult

        val service = TjenestepensjonSimuleringFoer1963Service(
            pidGetter = arrangePidGetter(pid),
            tjenestepensjonSimuleringClient = tjenestepensjonSimuleringClient,
            loependeVedtakService = arrangeLoependeVedtak(
                harUfoeretrygd = false,
                harTidsbegrensetOffentligAfp = false
            ),
            ekskluderingFacade = arrangeEkskludering(erApoteker = false),
            tpclient = mockk()
        )

        service.hentTjenestepensjonSimulering(spec) shouldBe expectedResult

        verify { tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(spec, pid) }
    }
})

private fun createSpec(foedselsdato: LocalDate) =
    SimuleringOffentligTjenestepensjonFoer1963Spec(
        simuleringType = SimuleringType.ALDERSPENSJON,
        sivilstand = Sivilstand.UGIFT,
        foedselsdato = foedselsdato,
        eps = EpsSpec(levende = LevendeEps(harInntektOver2G = false, harPensjon = false)),
        forventetAarligInntektFoerUttak = 500000,
        gradertUttak = null,
        heltUttak = HeltUttak(
            uttakFomAlder = Alder(aar = 67, maaneder = 0),
            inntekt = Inntekt(aarligBeloep = 0, tomAlder = Alder(aar = 75, maaneder = 0))
        ),
        utenlandsopphold = Utenlandsopphold(
            periodeListe = emptyList(),
            antallAar = 0
        ),
        afpInntektMaanedFoerUttak = false,
        afpOrdning = null,
        afpInntektMndForUttak = false,
        stillingsprosentOffHeltUttak = "100",
        stillingsprosentOffGradertUttak = null
    )

private fun arrangePidGetter(pid: Pid): PidGetter =
    mockk<PidGetter>().apply {
        every { pid() } returns pid
    }

private fun arrangeLoependeVedtak(
    harUfoeretrygd: Boolean,
    harTidsbegrensetOffentligAfp: Boolean
): LoependeVedtakService =
    mockk<LoependeVedtakService>().apply {
        every { hentLoependeVedtak() } returns VedtakSamling(
            loependeAlderspensjon = null,
            fremtidigAlderspensjon = null,
            ufoeretrygd = if (harUfoeretrygd) {
                LoependeUfoeretrygd(
                    grad = 100,
                    fom = LocalDate.of(2020, 1, 1)
                )
            } else null,
            privatAfp = null,
            pre2025OffentligAfp = if (harTidsbegrensetOffentligAfp) {
                LoependeEntitet(fom = LocalDate.of(2024, 1, 1))
            } else null
        )
    }

private fun arrangeEkskludering(erApoteker: Boolean): EkskluderingFacade =
    mockk<EkskluderingFacade>().apply {
        every { apotekerEkskludering() } returns if (erApoteker) {
            EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        } else {
            EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)
        }
    }

private fun arrangeTpClient(pid: Pid, tpOrdninger: List<String>): TpTjenestepensjonClient =
    mockk<TpTjenestepensjonClient>().apply {
        every { tjenestepensjonsforhold(pid) } returns Tjenestepensjonsforhold(tpOrdninger)
    }

