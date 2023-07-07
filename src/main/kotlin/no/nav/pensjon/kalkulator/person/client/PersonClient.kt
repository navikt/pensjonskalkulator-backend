package no.nav.pensjon.kalkulator.person.client

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid

interface PersonClient {
    fun getPerson(pid: Pid): Person?
}
