package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val client: PersonClient,
    private val pidGetter: PidGetter
) {

    fun getPerson() = client.fetchPerson(pidGetter.pid())
}
