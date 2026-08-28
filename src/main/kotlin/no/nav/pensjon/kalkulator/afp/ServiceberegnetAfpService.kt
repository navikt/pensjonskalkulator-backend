package no.nav.pensjon.kalkulator.afp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.client.ServiceberegnetAfpClient
import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ServiceberegnetAfpService(
    private val client: ServiceberegnetAfpClient,
    private val opptjeningClient: PensjonspoengClient,
    private val pidGetter: PidGetter,
    private val epsService: EpsService,
    private val personService: PersonService
) {
    private val log = KotlinLogging.logger {}

    fun simulerServiceberegnetAfp(spec: InternServiceberegnetAfpSpec): ServiceberegnetAfpResult =
        try {
            val pid = pidGetter.pid()

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
                opptjeningFolketrygden = registrertOpptjeningListe(pid, spec),
                epsMottarPensjon = spec.epsMottarPensjon,
                epsInntektOver2G = spec.epsInntektOver2G,
                tidligereGiftEllerBarnMedSamboer = epsService.tidligereGiftEllerBarnMedSamboer(),
                sivilstatus = spec.sivilstatus,
                registrertSivilstatus = personService.getPerson().sivilstand
            )

            log.debug { "Simulerer serviceberegnet AFP for afpOrdning=${domainSpec.afpOrdning}, uttaksdato=${domainSpec.uttaksdato}" }

            client.simulerServiceberegnetAfp(domainSpec)
        } catch (e: EgressException) {
            log.error(e) { "Feil ved simulering av serviceberegnet AFP" }
            throw e
        }

    private fun registrertOpptjeningListe(pid: Pid, spec: InternServiceberegnetAfpSpec): List<OpptjeningAar> =
        opptjeningClient.fetchOpptjeningOgBeholdning(pid).first
            .map(::mapOpptjeningAar) + mapInntektOpptjening(spec)

    private fun mapOpptjeningAar(dto: AarligOpptjening) =
        OpptjeningAar(
            ar = dto.aar,
            pensjonsgivendeInntekt = dto.pensjonsgivendeInntekt,
            registrertePensjonspoeng = dto.pensjonspoeng,
            omsorgspoeng = dto.omsorgspoeng?.toDouble(),
            maksUforegrad = dto.maksimalUfoeregrad,
        )

    private fun mapInntektOpptjening(dto: InternServiceberegnetAfpSpec): List<OpptjeningAar> =
        listOfNotNull(dto.inntektForrigeKalenderaar?.let {
            OpptjeningAar(LocalDate.now().year - 1, it, registrertePensjonspoeng = null, omsorgspoeng = null, maksUforegrad = null)
        }) +
        (dto.inntektFremTilUttak?.let { inntekt ->
            (LocalDate.now().year until dto.uttaksdato.year).map { year ->
                OpptjeningAar(year, inntekt, registrertePensjonspoeng = null, omsorgspoeng = null, maksUforegrad = null)
            }
        } ?: emptyList())
}