package no.nav.pensjon.kalkulator.merknad.client

import no.nav.pensjon.kalkulator.merknad.Merknader
import no.nav.pensjon.kalkulator.person.Pid

interface MerknadClient {
    fun fetchMerknader(pid: Pid): Merknader
}