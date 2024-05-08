package no.nav.pensjon.kalkulator.ufoere.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.ufoere.Ufoeregrad

interface UfoeregradClient {
    fun hentUfoeregrad(pid: Pid): Ufoeregrad
}
