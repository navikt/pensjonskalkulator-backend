package no.nav.pensjon.kalkulator.sak.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.sak.Sak
import no.nav.pensjon.kalkulator.sak.SakType

interface SakClient {
    fun fetchSaker(pid: Pid): List<Sak>

    suspend fun fetchSakerAsync(pid: Pid): List<Sak>

    fun opprettNySak(pid: Pid, sakstype: SakType): Sak
}
