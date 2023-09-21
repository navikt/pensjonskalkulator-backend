package no.nav.pensjon.kalkulator.sak

import no.nav.pensjon.kalkulator.person.Pid

interface SakClient {
    fun fetchSaker(pid: Pid): List<Sak>
}
