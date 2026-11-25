package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.acl

import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.TilgangResult
import no.nav.pensjon.kalkulator.tech.trace.TraceAid

/**
 * Responsbody ved tilgangsnekt.
 *
 * 'type' skal hjelpe frontend å skille tilgangsnekt fra andre feiltyper,
 * mens 'detail' er menneskelesbar tekst som ikke må brukes til logikk.
 */
data class TilgangsnektDto(
    val type: AvvisningAarsakDto,
    val detail: String? = null,
    val traceId: String? = null
) {
    companion object {
        fun transferable(source: TilgangResult) =
            TilgangsnektDto(
                type = AvvisningAarsakDto.fromInternalValue(source.avvisningAarsak),
                detail = source.begrunnelse,
                traceId = source.traceId ?: TraceAid.traceId()
            )

        fun transferable(feilmelding: String) =
            TilgangsnektDto(
                type = AvvisningAarsakDto.TILGANGSSJEKK_FEIL,
                detail = feilmelding,
                traceId = TraceAid.traceId()
            )
    }
}