package no.nav.pensjon.kalkulator.afp

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.afp.api.dto.InternServiceberegnetAfpSpec
import no.nav.pensjon.kalkulator.afp.api.map.ServiceberegnetAfpApiMapper
import no.nav.pensjon.kalkulator.afp.client.ServiceberegnetAfpClient
import no.nav.pensjon.kalkulator.opptjening.client.OpptjeningsgrunnlagClient
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.opptjening.client.popp.PoppPensjonspoengClient
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.person.relasjon.eps.client.EpsClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.stereotype.Service

@Service
class ServiceberegnetAfpService(
    private val client: ServiceberegnetAfpClient,
    private val pensjonspoengClient: PensjonspoengClient,
    private val pidGetter: PidGetter,
    private val epsService: EpsService,
) {
    private val log = KotlinLogging.logger {}

    fun simulerServiceberegnetAfp(spec: InternServiceberegnetAfpSpec): ServiceberegnetAfpResult =
        try {
            val pid = pidGetter.pid()
            val pensjonspoeng = pensjonspoengClient.fetchPensjonspoeng(pid)
            val tidligereGiftEllerBarnMedSamboer = epsService.tidligereGiftEllerBarnMedSamboer()
            val domainSpec = ServiceberegnetAfpApiMapper.fromDto(spec, pid, pensjonspoeng, tidligereGiftEllerBarnMedSamboer)

            log.debug { "Simulerer serviceberegnet AFP for afpOrdning=${domainSpec.afpOrdning}, uttaksdato=${domainSpec.uttaksdato}" }
            client.simulerServiceberegnetAfp(domainSpec)
        } catch (e: EgressException) {
            log.error(e) { "Feil ved simulering av serviceberegnet AFP" }
            throw e
        }
}
