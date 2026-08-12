package no.nav.pensjon.kalkulator.simulering

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblem
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpProblemType
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpService
import no.nav.pensjon.kalkulator.afp.InternServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.client.MerknadClient
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
    private val serviceberegnetAfpService: ServiceberegnetAfpService,
    private val merknadClient: MerknadClient
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
     * simulerPensjon = simulerPersonligAlderspensjon + merknader & improved handling of problems.
     */
    fun simulerPensjon(providedSpec: ImpersonalSimuleringSpec): SimuleringResult {
        if (providedSpec.simuleringType == SimuleringType.SERVICEBEREGN_AFP)
            return simulerAfpMedFpp(providedSpec)

        val result = simulerAlderspensjon(providedSpec)

        val opptjeningComboListe: List<SimulertOpptjening> = merge(
            opptjeningListe = result.opptjeningListe,
            merknaderPerAar = merknadClient.fetchMerknader(pid = pidGetter.pid()).perAar
        )

        return result.withOpptjeningListe(opptjeningComboListe)
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
            problem(e, type = ProblemType.ANNEN_SERVERFEIL)
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
                vilkaarsproeving = Vilkaarsproeving(
                    innvilget = afpResult.beregnetAfp != null && afpResult.problem == null
                ),
                harForLiteTrygdetid = false,
                trygdetid = 0,
                opptjeningListe = afpResult.opptjeningListe.map(::simulert), alderAar = null,
                problem = afpResult.problem?.let(::mapAfpProblem)
            )
        } catch (e: BadRequestException) {
            problem(e, type = ProblemType.ANNEN_KLIENTFEIL)
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

        private fun merge(
            opptjeningListe: List<SimulertOpptjening>,
            merknaderPerAar: Map<Int, List<MerknadCode>>
        ): List<SimulertOpptjening> {
            val nonEmptyMerknaderPerAar = merknaderPerAar.filter { it.value.isNotEmpty() }
            val foersteMerknadAar = nonEmptyMerknaderPerAar.minOfOrNull { it.key } ?: 9999
            val sisteMerknadAar = nonEmptyMerknaderPerAar.maxOfOrNull { it.key } ?: 0
            val foersteAar = minAar(opptjeningListe).coerceAtMost(foersteMerknadAar)
            val sisteAar = maxAar(opptjeningListe).coerceAtLeast(sisteMerknadAar)
            if (foersteAar > sisteAar) return emptyList()

            val liste = mutableListOf<SimulertOpptjening>()

            for (aar in foersteAar..sisteAar) {
                merknaderPerAar[aar].orEmpty().let {
                    liste.add(
                        opptjening(opptjeningListe, aar)?.withMerknadListe(it) ?: bareMerknader(aar, merknadListe = it)
                    )
                }
            }

            return liste
        }

        private fun simulert(opptjening: AarligOpptjening) =
            SimulertOpptjening(
                aarstall = opptjening.aar,
                pensjonsgivendeInntektBeloep = opptjening.pensjonsgivendeInntekt,
                pensjonspoeng = opptjening.pensjonspoeng,
                pensjonsbeholdningBeloep = opptjening.beholdning,
                merknadListe = opptjening.merknadListe
            )

        private fun opptjening(opptjeningListe: List<SimulertOpptjening>, aar: Int): SimulertOpptjening? =
            opptjeningListe.firstOrNull { it.aarstall == aar }

        private fun minAar(aarligListe: List<SimulertOpptjening>): Int =
            aarligListe.minOfOrNull { it.aarstall } ?: 9999

        private fun maxAar(aarligListe: List<SimulertOpptjening>): Int =
            aarligListe.maxOfOrNull { it.aarstall } ?: 0

        private fun bareMerknader(aar: Int, merknadListe: List<MerknadCode>) =
            SimulertOpptjening(
                aarstall = aar,
                pensjonsgivendeInntektBeloep = 0,
                pensjonspoeng = 0.0,
                pensjonsbeholdningBeloep = 0,
                merknadListe
            )

        private fun problem(e: RuntimeException, type: ProblemType) =
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