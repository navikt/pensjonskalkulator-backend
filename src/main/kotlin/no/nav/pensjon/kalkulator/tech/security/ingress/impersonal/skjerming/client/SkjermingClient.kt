package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client

import no.nav.pensjon.kalkulator.person.Pid

interface SkjermingClient {

    fun personErTilgjengelig(pid: Pid): Boolean
}
