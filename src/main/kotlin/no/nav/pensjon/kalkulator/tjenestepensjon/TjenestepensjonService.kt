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

    fun hentMedlemskapITjenestepensjonsordninger() =
        tjenestepensjonClient.tjenestepensjonsforhold(pidGetter.pid()).tpOrdninger

    fun harAfpOffentligLivsvarig() = hentTpOrdningHvorBrukerHarAfpOffentligLivsvarig().isNotEmpty()

    fun hentAfpOffentligLivsvarigDetaljer(): AfpOffentligLivsvarigResult =
        hentAfpOffentligLivsvarigDetaljer(medTpOrdning = false)

    fun hentAfpOffentligLivsvarigDetaljerMedTpOrdning(): AfpOffentligLivsvarigResult =
        hentAfpOffentligLivsvarigDetaljer(medTpOrdning = true)

    private fun hentAfpOffentligLivsvarigDetaljer(medTpOrdning: Boolean): AfpOffentligLivsvarigResult {
        val tpNumre = hentTpOrdningHvorBrukerHarAfpOffentligLivsvarig()

        if (tpNumre.isEmpty()) {
            log.info { "Bruker har ingen livsvarig offentlig AFP-ordning" }
            return AfpOffentligLivsvarigResult(
                afpInnvilget = null,
                virkningFom = null,
                maanedligBeloepListe = emptyList(),
                sistBenyttetGrunnbeloep = null
            )
        }

        if (tpNumre.size > 1) {
            log.error { "Bruker har flere ordninger for livsvarig offentlig AFP: $tpNumre" }
            throw EgressException("Bruker har flere ordninger for livsvarig offentlig AFP (${tpNumre.size}). Dette er ikke støttet.")
        }

        val tpNr = tpNumre.first()
        val uttaksdato = LocalDate.now().plusMonths(1).withDayOfMonth(1)

        val pid = pidGetter.pid()
        val result: AfpOffentligLivsvarigResult = tjenestepensjonClient.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, uttaksdato)

        return if (medTpOrdning)
            result.copy(
                tpNummer = tpNr,
                tpOrdningNavn = tjenestepensjonClient.hentTpOrdningNavn(tpNr)
            )
        else
            result
    }

    fun erApoteker(): Boolean =
        if (featureToggleService.isEnabled("mock-norsk-pensjon") && pidGetter.pid().value == "18870199488")
            true
        else
            tjenestepensjonClient.erApoteker(pidGetter.pid())

    private fun hentTpOrdningHvorBrukerHarAfpOffentligLivsvarig(): List<String> =
            tjenestepensjonClient.afpOffentligLivsvarigTpNummerListe(pidGetter.pid())

    private companion object {
        private fun harForhold(tjenestepensjon: Tjenestepensjon): Boolean =
            tjenestepensjon.forholdList.isNotEmpty()
    }
}
