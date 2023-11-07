package no.nav.pensjon.kalkulator.person.client

import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid

interface PersonClient {
    fun fetchPerson(pid: Pid): Person?

    fun fetchAdressebeskyttelse(pid: Pid): Person?
}
