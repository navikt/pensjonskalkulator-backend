package no.nav.pensjon.kalkulator.opptjening.client

import no.nav.pensjon.kalkulator.opptjening.Opptjeningsgrunnlag
import no.nav.pensjon.kalkulator.person.Pid

interface OpptjeningsgrunnlagClient {
    fun fetchOpptjeningsgrunnlag(pid: Pid): Opptjeningsgrunnlag
}
