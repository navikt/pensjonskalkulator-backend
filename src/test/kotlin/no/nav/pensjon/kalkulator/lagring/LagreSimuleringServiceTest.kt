package no.nav.pensjon.kalkulator.lagring

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.lagring.client.LagreSimuleringClient
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType

class LagreSimuleringServiceTest : ShouldSpec({

    val sakService = mockk<SakService>()
    val client = mockk<LagreSimuleringClient>()
    val service = LagreSimuleringService(sakService, client)

    should("hente sakId og delegere lagring til klient") {
        val simulering = simulering()
        val response = lagreSimuleringResponse()
        every { sakService.hentEllerOpprettSak(SakType.ALDERSPENSJON) } returns SAK_ID
        every { client.lagreSimulering(SAK_ID, simulering) } returns response

        service.lagreSimulering(simulering) shouldBe response

        verify(exactly = 1) { sakService.hentEllerOpprettSak(SakType.ALDERSPENSJON) }
        verify(exactly = 1) { client.lagreSimulering(SAK_ID, simulering) }
    }

    should("returnere respons fra klient") {
        val simulering = simulering()
        val expected = LagreSimuleringResponse(brevId = "brev-999", sakId = "sak-999")
        every { sakService.hentEllerOpprettSak(SakType.ALDERSPENSJON) } returns SAK_ID
        every { client.lagreSimulering(SAK_ID, simulering) } returns expected

        service.lagreSimulering(simulering) shouldBe expected
    }
}) {
    companion object {
        private const val SAK_ID = 42L

        private fun simulering() = LagreSimulering(
            alderspensjonListe = listOf(LagreAlderspensjon(alderAar = 67, beloep = 250000, gjenlevendetillegg = null)),
            livsvarigOffentligAfpListe = null,
            tidsbegrensetOffentligAfp = null,
            privatAfpListe = null,
            vilkaarsproevingsresultat = LagreVilkaarsproevingsresultat(erInnvilget = true, alternativ = null),
            trygdetid = null,
            pensjonsgivendeInntektListe = null,
            simuleringsinformasjon = null,
            enhetsId = "4817"
        )

        private fun lagreSimuleringResponse() =
            LagreSimuleringResponse(brevId = "brev-123", sakId = "sak-456")
    }
}
