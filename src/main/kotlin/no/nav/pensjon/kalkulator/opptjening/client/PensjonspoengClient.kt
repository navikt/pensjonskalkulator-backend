package no.nav.pensjon.kalkulator.opptjening.client

import no.nav.pensjon.kalkulator.opptjening.AarligBeholdning
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.person.Pid

interface PensjonspoengClient {

    fun fetchOpptjeningOgBeholdning(pid: Pid): Pair<List<AarligOpptjening>, List<AarligBeholdning>>
}