package no.nav.pensjon.kalkulator.person.relasjon.eps.client

import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus

data class NaavaerendeEpsSpec(
    val soekerPid: Pid,
    val personalia: List<PersonaliaType>
)

data class NyligsteEpsSpec(
    val soekerPid: Pid,
    val sivilstatus: Sivilstatus,
    val personalia: List<PersonaliaType>
)

data class TidligereStatusSpec(
    val soekerPid: Pid,
    val samboerPid: Pid
)