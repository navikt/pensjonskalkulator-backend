package no.nav.pensjon.kalkulator.person.relasjon.eps

import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
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

    fun tidligereGiftEllerBarnMedSamboer(): Boolean? {
        val eps = hentNaavaerendeEps()

        return if (
            eps?.relasjonstype == Relasjonstype.SAMBOER &&
            eps.pid != null
        ) {
            client.fetchTidligereGiftEllerBarnMed(
                soekerPid = pidGetter.pid(),
                samboerPid = eps.pid
            )
        } else {
            null
        }
    }

    private fun hentNaavaerendeEps() =
        try {
            client.fetchNaavaerendeEps(
                soekerPid = pidGetter.pid(),
                personaliaSpec
            )
        } catch (e: EgressException) {
            if (e.statusCode == HttpStatus.NOT_FOUND) {
                null
            } else {
                throw e
            }
        }

    private fun relasjonstype(): Relasjonstype =
        hentNaavaerendeEps()?.relasjonstype ?: Relasjonstype.UKJENT

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