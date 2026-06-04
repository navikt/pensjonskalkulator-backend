package no.nav.pensjon.kalkulator.opptjening.client

import no.nav.pensjon.kalkulator.opptjening.Pensjonspoeng
import no.nav.pensjon.kalkulator.person.Pid

interface PensjonspoengClient {
    fun fetchPensjonspoeng(pid: Pid): List<Pensjonspoeng>
}
