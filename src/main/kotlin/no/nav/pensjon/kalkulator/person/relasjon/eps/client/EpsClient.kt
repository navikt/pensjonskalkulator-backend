package no.nav.pensjon.kalkulator.person.relasjon.eps.client

import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon

/**
 * EPS = ektefelle/partner/samboer
 */
interface EpsClient {

    fun fetchNaavaerendeEps(
        soekerPid: Pid,
        personaliaSpec: List<PersonaliaType>
    ): Familierelasjon

    fun fetchNyligsteEps(
        soekerPid: Pid,
        sivilstatus: Sivilstatus,
        personaliaSpec: List<PersonaliaType>
    ): Familierelasjon
}