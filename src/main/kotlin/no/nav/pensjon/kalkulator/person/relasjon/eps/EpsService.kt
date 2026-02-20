package no.nav.pensjon.kalkulator.person.relasjon.eps

import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

/**
 * Ref. PSELV: PersonopplysningerAction.findSisteEpsRelasjon
 */
@Service
class EpsService(
    private val client: EpsClient,
    private val pidGetter: PidGetter
) {
    fun nyligsteEps(sivilstatus: Sivilstand): Familierelasjon =
        client.fetchNyligsteEps(
            soekerPid = pidGetter.pid(),
            sivilstatus,
            personaliaSpec
        )

    private companion object {
        private val personaliaSpec: List<PersonaliaType> = listOf(
            PersonaliaType.NAVN,
            PersonaliaType.FOEDSELSDATO,
            PersonaliaType.DOEDSDATO,
            PersonaliaType.STATSBORGERSKAP
        )
    }
}