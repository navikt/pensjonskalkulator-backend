package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangClient
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client.TilgangResult
import org.springframework.stereotype.Service

@Service
class TilgangService(private val client: TilgangClient) {
    fun sjekkTilgang(pid: Pid): TilgangResult = client.sjekkTilgang(pid)
}
