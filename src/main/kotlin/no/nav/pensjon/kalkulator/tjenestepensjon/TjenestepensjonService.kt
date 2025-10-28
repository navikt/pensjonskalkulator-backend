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

    // Hent alle tpNr, hent uttaksdato, og la klienten gjøre kallene til alle leverandører parallelt.
    // Prioriter INNVILGET status, ellers returner første resultat
    fun hentAfpOffentligLivsvarigDetaljer(): AfpOffentligLivsvarigResult {
        val pid = pidGetter.pid()
        val tpNumre = tjenestepensjonClient.afpOffentligLivsvarigTpNummerListe(pid)

        if (tpNumre.isEmpty()) {
            log.info { "Bruker har ingen AFP offentlig livsvarig ordninger" }
            throw NotFoundException("Bruker har ingen AFP offentlig livsvarig ordninger")
        }

        log.debug { "Fant ${tpNumre.size} TP-numre: $tpNumre" }

        //TODO: Hent riktig uttaksdato og fjern hardkodet dato
        val uttaksdato = LocalDate.of(2026, 2, 1)

        log.debug { "Found uttaksdato: $uttaksdato" }

        // Gjør parallelle kall til alle leverandører - fanger exceptions per leverandør
        val resultater = runBlocking {
            tpNumre.map { tpNr ->
                async {
                    try {
                        log.debug { "Henter AFP detaljer for tpNr=$tpNr" }
                        tjenestepensjonClient.hentAfpOffentligLivsvarigDetaljer(pid, tpNr, uttaksdato)
                    } catch (e: Exception) {
                        log.warn(e) { "Feilet å hente AFP detaljer for tpNr=$tpNr: ${e.message}" }
                        null
                    }
                }
            }.mapNotNull { it.await() }
        }

        if (resultater.isEmpty()) {
            log.info { "Ingen AFP-data hentet for ${tpNumre.size} TP-ordning(er)" }
            throw NotFoundException("Kunne ikke hente AFP-data fra noen av brukerens tjenestepensjonsordninger")
        }

        log.debug { "Fikk hentet ${resultater.size} resultater fra TP ordninger" }

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
    }
}
