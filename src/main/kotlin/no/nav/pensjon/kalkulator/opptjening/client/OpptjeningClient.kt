package no.nav.pensjon.kalkulator.opptjening.client

import no.nav.pensjon.kalkulator.opptjening.Opptjeningshistorikk

interface OpptjeningClient {
    fun getOpptjeningshistorikk(spec: OpptjeningshistorikkSpec): Opptjeningshistorikk
}
