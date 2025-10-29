package no.nav.pensjon.kalkulator.tjenestepensjon

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TjenestepensjonService(
    private val tjenestepensjonClient: TjenestepensjonClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    private val log = KotlinLogging.logger {}

    fun harTjenestepensjonsforhold() = harForhold(tjenestepensjonClient.tjenestepensjon(pidGetter.pid()))

    fun hentMedlemskapITjenestepensjonsordninger() = tjenestepensjonClient.tjenestepensjonsforhold(pidGetter.pid()).tpOrdninger

    fun hentAfpOffentligLivsvarigDetaljer(): AfpOffentligLivsvarigResult {
        val pid = pidGetter.pid()
        val tpNumre = tjenestepensjonClient.afpOffentligLivsvarigTpNummerListe(pid)

        if (tpNumre.isEmpty()) {
            log.info { "Bruker har ingen AFP offentlig livsvarig ordninger" }
            throw EgressException("Bruker har ingen AFP offentlig livsvarig ordninger")
        }

        if (tpNumre.size > 1) {
            log.error { "Bruker har flere AFP offentlig livsvarig ordninger: $tpNumre" }
            throw EgressException("Bruker har flere AFP offentlig livsvarig ordninger (${tpNumre.size}). Dette er ikke støttet.")
        }

        val tpNr = tpNumre.first()
        val uttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)

        return tjenestepensjonClient.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, uttaksdato)
    }

    fun erApoteker(): Boolean =
        if (featureToggleService.isEnabled("mock-norsk-pensjon") && pidGetter.pid().value == "18870199488")
            true
        else
            tjenestepensjonClient.erApoteker(pidGetter.pid())

    private companion object {
        private fun harForhold(tjenestepensjon: Tjenestepensjon): Boolean =
            tjenestepensjon.forholdList.isNotEmpty()
    }
}
