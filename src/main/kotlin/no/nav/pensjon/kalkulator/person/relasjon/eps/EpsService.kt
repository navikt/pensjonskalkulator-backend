package no.nav.pensjon.kalkulator.person.relasjon.eps

import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.CacheAwarePopulasjonstilgangService
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class EpsService(
    private val client: EpsClient,
    private val personService: PersonService,
    private val pidGetter: PidGetter,
    private val populasjonstilgangService: CacheAwarePopulasjonstilgangService
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
    fun nyligsteRelasjon(sivilstatus: Sivilstatus): Familierelasjon {
        val eps: Familierelasjon = client.fetchNyligsteEps(
            soekerPid = pidGetter.pid(),
            sivilstatus,
            personaliaSpec
        )

        eps.pid?.let(populasjonstilgangService::eventuellTilgangsnektAarsak)?.let {
            throw AccessDeniedException("Tilgang til EPS nektet: $it")
        }

        return eps
    }

    private fun relasjonstype(): Relasjonstype =
        try {
            client.fetchNaavaerendeEps(soekerPid = pidGetter.pid(), personaliaSpec).relasjonstype
        } catch (e: EgressException) {
            if (e.statusCode == HttpStatus.NOT_FOUND)
                Relasjonstype.UKJENT
            else throw e
        }

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

        private fun erUkjent(sivilstand: Sivilstand?): Boolean =
            sivilstand == null || Sivilstand.UOPPGITT == sivilstand
    }
}