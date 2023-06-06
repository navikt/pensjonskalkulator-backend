package no.nav.pensjon.kalkulator.avtale.client.pen

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.pen.dto.PensjonsavtalerDto
import no.nav.pensjon.kalkulator.avtale.client.pen.map.PensjonsavtaleMapper
import no.nav.pensjon.kalkulator.pen.PenClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*
import no.nav.pensjon.kalkulator.tech.selftest.Pingable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class PenPensjonsavtaleClient(
    @Value("\${pen.url}") baseUrl: String,
    webClient: WebClient
) : PenClient(baseUrl, webClient), PensjonsavtaleClient, Pingable {
    override fun fetchAvtaler(pid: Pid): Pensjonsavtaler {
        val response = doGet(PATH, PensjonsavtalerDto::class.java)
        return PensjonsavtaleMapper.fromDto(response ?: emptyDto())
    }

    companion object {
        private const val PATH = "$BASE_PATH/pensjonsavtaler"

        private fun emptyDto() = PensjonsavtalerDto(emptyList())
    }
}
