package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import org.springframework.stereotype.Service

@Service
class PersonFacade(
    private val personService: PersonService,
    private val epsService: EpsService
) {
    fun getPerson(): Person =
        personService.getPerson().withSivilstatus(epsService.naavaerendeSivilstatus())
}
