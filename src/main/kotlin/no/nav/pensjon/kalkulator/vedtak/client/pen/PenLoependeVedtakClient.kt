package no.nav.pensjon.kalkulator.vedtak.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.vedtak.LoependeAlderspensjon
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling
import no.nav.pensjon.kalkulator.vedtak.Utbetaling
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.map.PenLoependeVedtakMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.time.LocalDate

@Component
class PenLoependeVedtakClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), LoependeVedtakClient {

    override fun hentLoependeVedtak(pid: Pid): VedtakSamling {
        if (pid.value == "01426347659") {
            return VedtakSamling(
                loependeAlderspensjon = LoependeAlderspensjon(
                    grad = 0,
                    fom = LocalDate.of(2025, 5, 1),
                    utbetalingSisteMaaned = Utbetaling(BigDecimal(0.0), LocalDate.of(2025, 9, 20)),
                    sivilstand = Sivilstand.GIFT
                ),
                fremtidigAlderspensjon = null,
                ufoeretrygd = null,
                privatAfp = null,
                pre2025OffentligAfp = null
            )
        }
        return doGet(
            object : ParameterizedTypeReference<PenLoependeVedtakDto>() {},
            path = PATH,
            pid
        )?.let(PenLoependeVedtakMapper::fromDto)
            ?: throw EgressException("Kunne ikke hente loepende vedtak for brukeren")
    }

    private companion object {
        private const val PATH = "simulering/vedtak/loependevedtak"
    }
}
