package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.common.client.pen.PenClient
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.PenUttaksalderResult
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.PenUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.map.PenUttaksalderResultMapper
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.map.PenUttaksalderSpecMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * NB: I pensjonskalkulator-backend blir TMU funnet ved kall til PENs 'simulering/alderspensjon'-tjeneste,
 * ikke til 'tidligst-mulig-uttak'-tjenesten.
 * Pr. juni 2024 er derfor PenUttaksalderClient ikke brukt.
 */
@Component
class PenUttaksalderClient(
    @Value("\${pen.url}") baseUrl: String,
    webClientBuilder: WebClient.Builder,
    traceAid: TraceAid,
    @Value("\${web-client.retry-attempts}") private val retryAttempts: String
) : PenClient(baseUrl, webClientBuilder, traceAid, retryAttempts), UttaksalderClient {

    override fun finnTidligsteUttaksalder(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec
    ) =
        doPost(
            path = PATH,
            requestBody = PenUttaksalderSpecMapper.toDto(impersonalSpec, personalSpec),
            requestClass = PenUttaksalderSpec::class.java,
            responseClass = PenUttaksalderResult::class.java,
            deprecatedBasePath = false
        )?.let(PenUttaksalderResultMapper::fromDto)

    private companion object {
        private const val PATH = "simulering/v1/tidligst-mulig-uttak"
    }
}
