package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.TilgangResult

interface PopulasjonstilgangClient {
    fun sjekkTilgang(pid: Pid): TilgangResult
}
