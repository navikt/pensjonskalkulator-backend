package no.nav.pensjon.kalkulator.tjenestepensjon

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class TjenestepensjonService(
    private val tjenestepensjonClient: TjenestepensjonClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    fun harTjenestepensjonsforhold() = harForhold(tjenestepensjonClient.tjenestepensjon(pidGetter.pid()))

    fun erApoteker() =
        if (featureToggleService.isEnabled("mock-norsk-pensjon") && pidGetter.pid().value == "18870199488")
            throw EgressException("Mock-feil", statusCode = HttpStatus.INTERNAL_SERVER_ERROR)
        else
            tjenestepensjonClient.erApoteker(pidGetter.pid())

    private companion object {
        private fun harForhold(tjenestepensjon: Tjenestepensjon): Boolean =
            tjenestepensjon.forholdList.isNotEmpty()
    }
}
