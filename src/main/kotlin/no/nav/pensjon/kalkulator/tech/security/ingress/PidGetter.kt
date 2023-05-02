package no.nav.pensjon.kalkulator.tech.security.ingress

import no.nav.pensjon.kalkulator.person.Pid

fun interface PidGetter {

    fun pid(): Pid
}
