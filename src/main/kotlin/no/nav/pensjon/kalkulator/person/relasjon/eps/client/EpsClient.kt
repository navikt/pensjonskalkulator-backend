package no.nav.pensjon.kalkulator.person.relasjon.eps.client

import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon

/**
 * EPS = ektefelle/partner/samboer
 */
interface EpsClient {

    fun fetchNyligsteEps(
        soekerPid: Pid,
        sivilstatus: Sivilstand,
        personaliaSpec: List<PersonaliaType>
    ): Familierelasjon
}