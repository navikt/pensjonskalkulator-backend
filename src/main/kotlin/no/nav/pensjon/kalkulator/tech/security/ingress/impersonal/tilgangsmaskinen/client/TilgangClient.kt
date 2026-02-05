package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.tilgangsmaskinen.client

import no.nav.pensjon.kalkulator.person.Pid

interface TilgangClient {
    fun sjekkTilgang(pid: Pid): TilgangResult
}
