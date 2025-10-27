package no.nav.pensjon.kalkulator.tjenestepensjon

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import no.nav.pensjon.kalkulator.tech.web.NotFoundException
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

    // Hent alle tpNr, finn uttaksdato fra TP-ytelser (datoYtelseIverksattFom for ALDER), og la klienten gjøre
    // kallene til alle leverandører parallelt. Prioriter INNVILGET status, ellers returner første resultat
    fun hentAfpOffentligLivsvarigDetaljer(): AfpOffentligLivsvarigResult {
        val pid = pidGetter.pid()
        val tpNumre = tjenestepensjonClient.afpOffentligLivsvarigTpNummerListe(pid)

        if (tpNumre.isEmpty()) {
            log.info { "Bruker har ingen AFP offentlig livsvarig ordninger" }
            throw NotFoundException("Bruker har ingen AFP offentlig livsvarig ordninger")
        }

        log.debug { "Found ${tpNumre.size} TP-numre: $tpNumre" }

        val uttaksdato = finnUttaksdato(tjenestepensjonClient.tjenestepensjon(pid))

        if (uttaksdato == null) {
            log.info { "Bruker har ikke startet uttak av alderspensjon" }
            throw NotFoundException("Bruker har ikke startet uttak av alderspensjon (ingen ALDER-ytelse funnet)")
        }

        log.debug { "Found uttaksdato: $uttaksdato" }

        // Gjør parallelle kall til alle leverandører
        val resultater = runBlocking {
            tpNumre.map { tpNr ->
                async {
                    try {
                        tjenestepensjonClient.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, uttaksdato)
                    } catch (e: Exception) {
                        log.warn(e) { "Failed to get AFP details for tpNr=$tpNr" }
                        null
                    }
                }
            }.mapNotNull { it.await() }
        }

        if (resultater.isEmpty()) {
            log.info { "Ingen AFP-data hentet fra noen leverandører for ${tpNumre.size} TP-ordning(er)" }
            throw NotFoundException("Kunne ikke hente AFP-data fra noen av brukerens tjenestepensjonsordninger")
        }

        log.debug { "Got ${resultater.size} results from providers" }

        // Prioriter første INNVILGET (afpStatus = true). Hvis ingen INNVILGET, returner første resultat
        return resultater.firstOrNull { it.afpStatus == true } ?: resultater.first()
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
