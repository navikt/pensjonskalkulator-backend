package no.nav.pensjon.kalkulator.afp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.client.ServiceberegnetAfpClient
import no.nav.pensjon.kalkulator.opptjening.AarligBeholdning
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.OpptjeningService
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import java.time.LocalDate
import org.springframework.stereotype.Service

@Service
class ServiceberegnetAfpService(
    private val client: ServiceberegnetAfpClient,
    private val pensjonspoengClient: PensjonspoengClient,
    private val pidGetter: PidGetter,
    private val epsService: EpsService,
    private val personService: PersonService
) {
    private val log = KotlinLogging.logger {}

    fun simulerServiceberegnetAfp(spec: InternServiceberegnetAfpSpec): ServiceberegnetAfpResult =
        try {
            val pid = pidGetter.pid()
            val opptjeningOgBeholdning = pensjonspoengClient.fetchOpptjeningOgBeholdning(pid)

            val domainSpec = ServiceberegnetAfpSpec(
                uttaksdato = spec.uttaksdato,
                fnr = pid.value,
                fodselsdato = spec.fodselsdato,
                afpOrdning = AfpOrdningType.valueOf(spec.afpOrdning),
                flyktning = spec.flyktning,
                antAarIUtlandet = spec.antAarIUtlandet,
                utenlandsopphold = spec.utenlandsopphold,
                forventetArbeidsinntekt = spec.forventetArbeidsinntekt,
                inntektMndForAfp = spec.inntektMndForAfp,
                opptjeningFolketrygden = opptjeningOgBeholdning.first.map(::opptjening) + pensjonsgivendeInntektListe(spec),
                epsMottarPensjon = spec.epsMottarPensjon,
                epsInntektOver2G = spec.epsInntektOver2G,
                tidligereGiftEllerBarnMedSamboer = epsService.tidligereGiftEllerBarnMedSamboer(),
                sivilstatus = spec.sivilstatus,
                registrertSivilstatus = personService.getPerson().sivilstand
            )

            log.debug { "Simulerer serviceberegnet AFP for afpOrdning=${domainSpec.afpOrdning}, uttaksdato=${domainSpec.uttaksdato}" }
            client.simulerServiceberegnetAfp(domainSpec).withOpptjening(opptjeningListe = merge(opptjeningOgBeholdning))
        } catch (e: EgressException) {
            log.error(e) { "Feil ved simulering av serviceberegnet AFP" }
            throw e
        }

    private companion object {

        private fun merge(
            pair: Pair<List<AarligOpptjening>, List<AarligBeholdning>>
        ): List<AarligOpptjening> =
            OpptjeningService.merge(opptjeningListe = pair.first, beholdningListe = pair.second)

        private fun pensjonsgivendeInntektListe(spec: InternServiceberegnetAfpSpec): List<OpptjeningAar> =
            listOfNotNull(spec.inntektForrigeKalenderaar?.let(::fjoraaretsPensjonsgivendeInntekt)) +
                    (spec.inntektFremTilUttak?.let { pensjonsgivendeInntektListe(uttaksaar = spec.uttaksdato.year, beloep = it) }
                        .orEmpty())

        private fun pensjonsgivendeInntektListe(uttaksaar: Int, beloep: Int): List<OpptjeningAar> =
            (LocalDate.now().year until uttaksaar).map { pensjonsgivendeInntekt(aar = it, beloep) }

        private fun fjoraaretsPensjonsgivendeInntekt(beloep: Int) =
            pensjonsgivendeInntekt(LocalDate.now().year - 1, beloep)

        private fun pensjonsgivendeInntekt(aar: Int, beloep: Int) =
            OpptjeningAar(
                ar = aar,
                pensjonsgivendeInntekt = beloep,
                registrertePensjonspoeng = null,
                omsorgspoeng = null,
                maksUforegrad = null
            )

        private fun opptjening(source: AarligOpptjening) =
            OpptjeningAar(
                ar = source.aar,
                pensjonsgivendeInntekt = source.pensjonsgivendeInntekt,
                registrertePensjonspoeng = source.pensjonspoeng,
                omsorgspoeng = source.omsorgspoeng?.toDouble(),
                maksUforegrad = source.maksimalUfoeregrad,
            )
    }
}