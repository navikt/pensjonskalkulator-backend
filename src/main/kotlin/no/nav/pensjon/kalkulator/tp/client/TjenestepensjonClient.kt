package no.nav.pensjon.kalkulator.tp.client

import no.nav.pensjon.kalkulator.person.Pid

interface TjenestepensjonClient {
    fun harTjenestepensjonsforhold(pid: Pid): Boolean
}
