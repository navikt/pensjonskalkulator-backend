package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.pen.PenClient
import no.nav.pensjon.kalkulator.uttaksalder.Uttaksalder
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderRequestDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.dto.UttaksalderResponseDto
import no.nav.pensjon.kalkulator.uttaksalder.client.pen.map.UttaksalderMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class PenUttaksalderClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient,
) : PenClient(baseUrl, webClient), UttaksalderClient {
    override fun finnTidligsteUttaksalder(spec: UttaksalderSpec): Uttaksalder? {
        val response = doPost(
            "$baseUrl${UTTAKSALDER_PATH}",
            UttaksalderMapper.toDto(spec),
            UttaksalderRequestDto::class.java,
            UttaksalderResponseDto::class.java,
        )

        return response?.let { UttaksalderMapper.fromDto(response) }
    }

    companion object {
        private const val UTTAKSALDER_PATH = "/pen/springapi/simulering/alderspensjon"

    }
}