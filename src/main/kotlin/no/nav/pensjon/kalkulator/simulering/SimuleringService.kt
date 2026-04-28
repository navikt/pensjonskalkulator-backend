package no.nav.pensjon.kalkulator.simulering

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpService
import no.nav.pensjon.kalkulator.afp.api.dto.InternServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.general.Alder
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
    private val log = KotlinLogging.logger {}

    fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult =
        simuleringClient.simulerAnonymAlderspensjon(spec)

    fun simulerPersonligAlderspensjon(impersonalSpec: ImpersonalSimuleringSpec): SimuleringResult {
        val personalSpec = PersonalSimuleringSpec(
            pid = pidGetter.pid(),
            sivilstatus = impersonalSpec.sivilstatus ?: sivilstatus(),
            aarligInntektFoerUttak = impersonalSpec.forventetAarligInntektFoerUttak
                ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
        )

        log.debug { "Simulerer med parametre $impersonalSpec og $personalSpec" }
        return simuleringClient
            .simulerPersonligAlderspensjon(impersonalSpec, personalSpec)
            .withAlderAar(naavaerendeAlder().aar)
    }

    /**
     * Same as simulerPersonligAlderspensjon but with improved handling of problems.
     */
    fun simulerPensjon(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        if (providedSpec.simuleringType == SimuleringType.SERVICEBEREGN_AFP) {
            simulerAfpMedFpp(providedSpec)
        } else {
            simulerAlderspensjon(providedSpec)
        }

    private fun simulerAlderspensjon(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        try {
            val registeredSpec = PersonalSimuleringSpec(
                pid = pidGetter.pid(),
                sivilstatus = providedSpec.sivilstatus ?: sivilstatus(),
                aarligInntektFoerUttak = providedSpec.forventetAarligInntektFoerUttak
                    ?: inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()
            )

            log.debug { "Simulerer med parametre $providedSpec og $registeredSpec" }

            simuleringClient
                .simulerPersonligAlderspensjon(providedSpec, registeredSpec)
                .withAlderAar(naavaerendeAlder().aar)
        } catch (e: BadRequestException) {
            problem(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: DateTimeParseException) {
            problem(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: NotFoundException) {
            problem(e, type = ProblemType.PERSON_IKKE_FUNNET)
        } catch (e: EgressException) {
            problem(e, type = ProblemType.SERVERFEIL)
        }

    private fun simulerAfpMedFpp(providedSpec: ImpersonalSimuleringSpec): SimuleringResult =
        try {
            val afpSpec = InternServiceberegnetAfpSpec(
                fodselsdato = personService.getPerson().foedselsdato,
                uttaksdato = providedSpec.heltUttak.uttakFomAlder?.let { uttakDato(foedselDato = personService.getPerson().foedselsdato, uttakAlder = it)} as LocalDate,
                afpOrdning = "AFPSTAT",
                flyktning = false,
                antAarIUtlandet = providedSpec.utenlandsopphold.antallAar,
                forventetArbeidsinntekt = providedSpec.forventetAarligInntektFoerUttak,
                inntektMndForAfp = providedSpec.afpFppInntektMndForAfp,
                opptjeningFolketrygden = emptyList()
            )

            val afpResult = serviceberegnetAfpService.simulerServiceberegnetAfp(afpSpec)

            SimuleringResult(
                alderspensjon = emptyList(),
                alderspensjonMaanedsbeloep = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = afpResult.beregnetAfp != null && afpResult.problem == null
                ),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList(),
                serviceberegnetAfpResult = afpResult.beregnetAfp,
                problem = afpResult.problem?.let { mapAfpProblem(it) }
            )
        } catch (e: BadRequestException) {
            problem(e, type = ProblemType.ANNEN_KLIENTFEIL)
        } catch (e: EgressException) {
            problem(e, type = ProblemType.ANNEN_SERVERFEIL)
        }

    private fun sivilstatus() =
        personService.getPerson().sivilstand.sivilstatus

    private fun naavaerendeAlder() =
        Alder.from(
            foedselDato = personService.getPerson().foedselsdato,
            dato = time.date()
        )

    private companion object {
        private fun problem(e: RuntimeException, type: ProblemType) =
            SimuleringResult(
                alderspensjon = emptyList(),
                alderspensjonMaanedsbeloep = null,
                pre2025OffentligAfp = null,
                afpPrivat = emptyList(),
                afpOffentlig = emptyList(),
                vilkaarsproeving = Vilkaarsproeving(innvilget = false),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningGrunnlagListe = emptyList(),
                problem = Problem(type, beskrivelse = e.message ?: "Ukjent feil - ${e.javaClass.simpleName}")
            )

        private fun mapAfpProblem(source: no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem) =
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
