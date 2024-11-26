package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.simulering.api.dto.AnonymSimuleringErrorV1
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringError

object PenAnonymSimuleringErrorMapper {

    fun fromDto(dto: PenAnonymSimuleringError): AnonymSimuleringErrorV1 = extractMessageFromException(dto)

    private fun extractMessageFromException(errorResponse: PenAnonymSimuleringError): AnonymSimuleringErrorV1 {
        val regex = """PEN(\d+\w*Exception):? ?(.+)?""".toRegex()
        val matchResult = regex.find(errorResponse.feilmelding)

        return matchResult?.let {
            val errorCode = ("PKU" + it.groups[1]?.value)
            val errorMessage = it.groups[2]?.value?.trim() ?: ""
            AnonymSimuleringErrorV1(status = errorCode, message = errorMessage)
        } ?: AnonymSimuleringErrorV1(
            status = "PKU500InternalServerError",
            message = "Det skjedde en feil i kalkuleringen"
        )
    }
}