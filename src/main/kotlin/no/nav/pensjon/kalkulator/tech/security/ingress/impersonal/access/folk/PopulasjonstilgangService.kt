package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.PopulasjonstilgangClient
import org.springframework.stereotype.Service

@Service
class PopulasjonstilgangService(private val client: PopulasjonstilgangClient) {
    private val log = KotlinLogging.logger {}

    fun sjekkTilgang(pid: Pid): TilgangResult =
        try {
            client.sjekkTilgang(pid)
        } catch (e: Exception) {
            // Enhver feil skal gi 'tilgang avvist'
            "Populasjonstilgangssjekk feilet".let {
                log.error(e) { "$it - ${e.message}" }

                TilgangResult(
                    innvilget = false,
                    avvisningAarsak = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEILET,
                    begrunnelse = "$it - se logg for detaljer",
                    traceId = null
                )
            }
        }
}
