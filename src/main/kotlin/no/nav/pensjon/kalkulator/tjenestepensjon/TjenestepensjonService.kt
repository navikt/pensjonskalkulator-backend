package no.nav.pensjon.kalkulator.tjenestepensjon

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tjenestepensjon.client.TjenestepensjonClient
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TjenestepensjonService(
    private val tjenestepensjonClient: TjenestepensjonClient,
    private val pidGetter: PidGetter,
    private val featureToggleService: FeatureToggleService
) {
    fun harTjenestepensjonsforhold() = harForhold(tjenestepensjonClient.tjenestepensjon(pidGetter.pid()))

    fun hentMedlemskapITjenestepensjonsordninger() = tjenestepensjonClient.tjenestepensjonsforhold(pidGetter.pid()).tpOrdninger

    // Hent første tpNr, finn uttaksdato fra TP-ytelser (datoYtelseIverksattFom for ALDER), og la klienten gjøre
    // de parallelle kallene og mappingen til status + beløp
    fun hentAfpOffentligLivsvarigDetaljer(): AfpOffentligLivsvarigResult? {
        val pid = pidGetter.pid()
        val tpNr = tjenestepensjonClient.afpOffentligLivsvarigTpNummerListe(pid).firstOrNull() ?: return null
        val uttaksdato = finnUttaksdato(tjenestepensjonClient.tjenestepensjon(pid)) ?: return null
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

        // Uttaksdato = første ALDER-ytelse med datoYtelseIverksattFom
        private fun finnUttaksdato(tp: Tjenestepensjon): LocalDate? =
            tp.forholdList.asSequence()
                .flatMap { it.ytelser.asSequence() }
                .firstOrNull { it.type.equals("ALDER", ignoreCase = true) && it.datoYtelseIverksattFom != null }
                ?.datoYtelseIverksattFom
    }
}
