package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjonsforhold
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClient
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.TpSimuleringClientTestObjects.spec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.*
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling

class TjenestepensjonSimuleringServiceTest : ShouldSpec({

    listOf(emptyList(), listOf("TP001", "TP002")).forEach { tpOrdninger ->
        should("map pension schemes $tpOrdninger when the person is excluded as a pharmacist") {
            val pidGetter = mockk<PidGetter>()
            val simuleringClient = mockk<TjenestepensjonSimuleringClient>()
            val vedtakService = mockk<LoependeVedtakService>()
            val ekskluderingFacade = mockk<EkskluderingFacade>()
            val tpClient = mockk<TpTjenestepensjonClient>()

            every { pidGetter.pid() } returns pid
            every { vedtakService.hentLoependeVedtak() } returns VedtakSamling(
                loependeAlderspensjon = null,
                fremtidigAlderspensjon = null,
                privatAfp = null,
                ufoeretrygd = null,
                avdoed = null
            )
            every { ekskluderingFacade.apotekerEkskludering() } returns
                    EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
            every { tpClient.tjenestepensjonsforhold(pid) } returns Tjenestepensjonsforhold(tpOrdninger)

            val service = TjenestepensjonSimuleringService(
                pidGetter = pidGetter,
                tjenestepensjonSimuleringClient = simuleringClient,
                loependeVedtakService = vedtakService,
                ekskluderingFacade = ekskluderingFacade,
                tpclient = tpClient
            )

            service.hentTjenestepensjonSimulering(spec) shouldBe OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(ResultatType.TP_ORDNING_STOETTES_IKKE),
                tpOrdninger = if (tpOrdninger.isEmpty()) emptyList() else listOf(
                    TpOrdning(tpOrdningNavn = "TP001", tpNummer = "TP001"),
                    TpOrdning(tpOrdningNavn = "TP002", tpNummer = "TP002")
                )
            )
            verify(exactly = 1) { tpClient.tjenestepensjonsforhold(pid) }
            verify(exactly = 0) { simuleringClient.hentTjenestepensjonSimulering(any(), any()) }
        }
    }
})
