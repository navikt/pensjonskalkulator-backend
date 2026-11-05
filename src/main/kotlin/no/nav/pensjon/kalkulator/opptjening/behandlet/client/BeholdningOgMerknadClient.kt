package no.nav.pensjon.kalkulator.opptjening.behandlet.client

import no.nav.pensjon.kalkulator.opptjening.behandlet.AarvisBeholdningOgMerknader
import no.nav.pensjon.kalkulator.person.Pid

interface BeholdningOgMerknadClient {
    fun fetchBeholdningOgMerknader(pid: Pid): AarvisBeholdningOgMerknader
}