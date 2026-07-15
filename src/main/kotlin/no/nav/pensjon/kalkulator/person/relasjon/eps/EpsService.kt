package no.nav.pensjon.kalkulator.person.relasjon.eps

import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.NaavaerendeEpsSpec
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.NyligsteEpsSpec
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.TidligereStatusSpec
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
            spec = NyligsteEpsSpec(
                soekerPid = pidGetter.pid(),
                sivilstatus,
                personalia = personaliaSpec
            )
        )

        eps.pid?.let(::eventuellTilgangsnektAarsak)
            ?.let { throw AccessDeniedException("Tilgang til EPS nektet: $it") }

        return eps
    }

    fun tidligereGiftEllerBarnMedSamboer(): Boolean? =
        hentNaavaerendeEps()?.let {
            if (it.relasjonstype == Relasjonstype.SAMBOER && it.pid != null)
                erTidligereGiftEllerHarBarnMed(it.pid)
            else
                null
        }

    private fun erTidligereGiftEllerHarBarnMed(pid: Pid): Boolean =
        client.fetchTidligereGiftEllerBarnMed(
            spec = TidligereStatusSpec(soekerPid = pidGetter.pid(), samboerPid = pid)
        )

    private fun hentNaavaerendeEps() =
        try {
            client.fetchNaavaerendeEps(
                spec = NaavaerendeEpsSpec(soekerPid = pidGetter.pid(), personalia = personaliaSpec)
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

    private fun eventuellTilgangsnektAarsak(pid: Pid): String? =
        populasjonstilgangService.eventuellTilgangsnektAarsak(pid = pid, sjekkKunKjerneregler = true)

    private companion object {
        private val personaliaSpec: List<PersonaliaType> =
            listOf(
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