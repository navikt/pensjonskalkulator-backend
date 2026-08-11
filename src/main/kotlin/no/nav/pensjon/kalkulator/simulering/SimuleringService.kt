package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpService
import no.nav.pensjon.kalkulator.afp.InternServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.time.TodayProvider
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.validity.Problem
import no.nav.pensjon.kalkulator.validity.ProblemType
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Service
class SimuleringService(
    private val simuleringClient: SimuleringClient,
    private val inntektService: InntektService,
    private val personService: PersonService,
    private val pidGetter: PidGetter,
    private val time: TodayProvider,
    private val serviceberegnetAfpService: ServiceberegnetAfpService
) {
    fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult =
        simuleringClient.simulerAnonymAlderspensjon(spec)

    fun simulerPersonligAlderspensjon(impersonalSpec: ImpersonalSimuleringSpec): SimuleringResult {
        val personalSpec = PersonalSimuleringSpec(
            pid = pidGetter.pid(),
            sivilstatus = impersonalSpec.sivilstatus ?: sivilstatus(),
            aarligInntektFoerUttak = impersonalSpec.forventetAarligInntektFoerUttak
                ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
        )

        return simuleringClient
            .simulerPersonligAlderspensjon(impersonalSpec, personalSpec)
            .withAlderAar(naavaerendeAlder().aar)
    }

    /**
     * Same as simulerPersonligAlderspensjon but with improved handling of problems.
     */
    fun simulerPensjon(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        if (providedSpec.simuleringType == SimuleringType.SERVICEBEREGN_AFP)
            simulerAfpMedFpp(providedSpec)
        else
            simulerAlderspensjon(providedSpec)


    private fun simulerAlderspensjon(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        try {
            val registeredSpec = PersonalSimuleringSpec(
                pid = pidGetter.pid(),
                sivilstatus = providedSpec.sivilstatus ?: sivilstatus(),
                aarligInntektFoerUttak = providedSpec.forventetAarligInntektFoerUttak
                    ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
            )

            simuleringClient
                .simulerPersonligAlderspensjon(providedSpec, registeredSpec)
                .withAlderAar(naavaerendeAlder().aar)
        } catch (e: BadRequestException) {
            problemResult(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: DateTimeParseException) {
            problemResult(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: NotFoundException) {
            problemResult(e, type = ProblemType.PERSON_IKKE_FUNNET)
        } catch (e: EgressException) {
            problemResult(e, type = ProblemType.ANNEN_SERVERFEIL)
        }

    private fun simulerAfpMedFpp(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        try {
            val afpSpec = InternServiceberegnetAfpSpec(
                fodselsdato = personService.getPerson().foedselsdato,
                uttaksdato = uttaksdato(providedSpec),
                afpOrdning = AfpOrdningType.AFPSTAT.name,
                flyktning = false,
                antAarIUtlandet = providedSpec.utenlandsopphold.antallAar,
                utenlandsopphold = providedSpec.utenlandsopphold.periodeListe,
                forventetArbeidsinntekt = providedSpec.gradertUttak?.aarligInntekt,
                inntektMndForAfp = providedSpec.inntektMaanedFoerAfp,
                inntektForrigeKalenderaar = providedSpec.inntektForrigeKalenderaar,
                inntektFremTilUttak = providedSpec.inntektFremTilUttak,
                epsMottarPensjon = providedSpec.eps.levende?.harPensjon,
                epsInntektOver2G = providedSpec.eps.levende?.harInntektOver2G,
                sivilstatus = providedSpec.sivilstatus,
            )

            val afpResult = serviceberegnetAfpService.simulerServiceberegnetAfp(afpSpec)

            SimuleringResult(
                alderspensjonListe = emptyList(),
                alderspensjonMaanedsbeloep = null,
                maanedligAlderspensjonForKnekkpunkter = null,
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                serviceberegnetAfp = afpResult.beregnetAfp,
                privatAfpListe = emptyList(),
                vilkaarsproeving = vilkaarsproevingsresultat(afpResult),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningListe = afpResult.opptjeningListe.map(::opptjening),
                alderAar = null,
                problem = afpResult.problem?.let(::mapAfpProblem)
            )
        } catch (e: BadRequestException) {
            problemResult(e, type = ProblemType.ANNEN_KLIENTFEIL)
        }
    //TODO: Fiks når feilkodenavn er klar
        /*catch (e: EgressException) {
            problem(e, type = ProblemType.ANNEN_SERVERFEIL)
        }*/

    private fun sivilstatus() =
        personService.getPerson().sivilstand.sivilstatus

    private fun naavaerendeAlder() =
        Alder.from(
            foedselDato = personService.getPerson().foedselsdato,
            dato = time.date()
        )

    private fun uttaksdato(spec: ImpersonalSimuleringSpec): LocalDate =
        spec.gradertUttak?.uttakFomAlder?.let(::uttaksdato)
            ?: throw BadRequestException("startalder for gradert uttak må angis")

    private fun uttaksdato(alder: Alder): LocalDate =
        uttakDato(
            foedselDato = personService.getPerson().foedselsdato,
            uttakAlder = alder
        )

    private companion object {

        private fun opptjening(source: AarligOpptjening) =
            SimulertOpptjening(
                aarstall = source.aar,
                pensjonsgivendeInntektBeloep = source.pensjonsgivendeInntekt,
                pensjonspoeng = source.pensjonspoeng,
                pensjonsbeholdningBeloep = source.beholdning
            )

        private fun vilkaarsproevingsresultat(afpResult: ServiceberegnetAfpResult) =
            Vilkaarsproeving(
                innvilget = afpResult.beregnetAfp != null && afpResult.problem == null
            )

        private fun problemResult(e: RuntimeException, type: ProblemType) =
            SimuleringResult(
                alderspensjonListe = emptyList(),
                alderspensjonMaanedsbeloep = null,
                maanedligAlderspensjonForKnekkpunkter = null,
                livsvarigOffentligAfpListe = emptyList(),
                tidsbegrensetOffentligAfp = null,
                serviceberegnetAfp = null,
                privatAfpListe = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningListe = emptyList(),
                alderAar = null,
                problem = Problem(type, beskrivelse = e.message ?: e.javaClass.simpleName)
            )

        private fun mapAfpProblem(source: ServiceberegnetAfpProblem) =
            Problem(
                type = when (source.type) {
                    ServiceberegnetAfpProblemType.UTILSTREKKELIG_TRYGDETID -> ProblemType.UTILSTREKKELIG_TRYGDETID
                    ServiceberegnetAfpProblemType.UTILSTREKKELIG_OPPTJENING -> ProblemType.UTILSTREKKELIG_OPPTJENING
                    ServiceberegnetAfpProblemType.ANNEN_KLIENTFEIL -> ProblemType.ANNEN_KLIENTFEIL
                },
                beskrivelse = source.beskrivelse
            )
    }
}