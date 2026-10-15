package no.nav.pensjon.kalkulator.opptjening.client

import no.nav.pensjon.kalkulator.opptjening.DatertBeholdning
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.person.Pid

interface PensjonspoengOgBeholdningClient {

    fun fetchPensjonspoengOgBeholdning(pid: Pid): Pair<List<AarligOpptjening>, List<DatertBeholdning>>
}