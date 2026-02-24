package no.nav.pensjon.kalkulator.person.relasjon.eps

import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class EpsService(
    private val client: EpsClient,
    private val personService: PersonService,
    private val pidGetter: PidGetter
) {
    /**
     * Ref. PSELV: PersonopplysningerActionDelegate.setBrukersSivilstatus
     */
    fun naavaerendeSivilstatus(): Sivilstatus =
        sivilstatus(
            registrertSivilstand = personService.getPerson().sivilstand,
            harSamboer = relasjonstype() == Relasjonstype.SAMBOER
        ) ?: Sivilstatus.UOPPGITT

    /**
     * Ref. PSELV: PersonopplysningerAction.findSisteEpsRelasjon
     */
    fun nyligsteRelasjon(sivilstatus: Sivilstatus): Familierelasjon =
        client.fetchNyligsteEps(
            soekerPid = pidGetter.pid(),
            sivilstatus,
            personaliaSpec
        )

    private fun relasjonstype(): Relasjonstype =
        client.fetchNaavaerendeEps(soekerPid = pidGetter.pid(), personaliaSpec).relasjonstype

    private companion object {
        private val personaliaSpec: List<PersonaliaType> = listOf(
            PersonaliaType.NAVN,
            PersonaliaType.FOEDSELSDATO,
            PersonaliaType.DOEDSDATO,
            PersonaliaType.STATSBORGERSKAP
        )

        private fun sivilstatus(registrertSivilstand: Sivilstand?, harSamboer: Boolean): Sivilstatus? =
            when {
                harSamboer.not() && erUkjent(registrertSivilstand) -> Sivilstatus.UGIFT
                harSamboer && registrertSivilstand?.allowsSamboer == true -> Sivilstatus.SAMBOER
                else -> registrertSivilstand?.sivilstatus
            }

        private fun erUkjent(sivilstatus: Sivilstand?): Boolean =
            sivilstatus == null || Sivilstand.UOPPGITT == sivilstatus
    }
}