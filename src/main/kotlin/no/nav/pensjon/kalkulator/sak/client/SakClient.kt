package no.nav.pensjon.kalkulator.sak.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.sak.Sak

interface SakClient {
    fun fetchSaker(pid: Pid): List<Sak>

    suspend fun fetchSakerAsync(pid: Pid): List<Sak>
}
