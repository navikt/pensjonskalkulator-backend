package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig

import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.client.PersonClient
import org.springframework.stereotype.Service

@Service
class FortroligAdresseService(private val personClient: PersonClient) {

    fun adressebeskyttelseGradering(pid: Pid): AdressebeskyttelseGradering =
        personClient.fetchAdressebeskyttelse(pid)?.adressebeskyttelse ?: AdressebeskyttelseGradering.UNKNOWN
}
