package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

data class OpprettBrevRequestDtoV1<out T : SaksbehandlerValgBrevdata>(
        val saksId: Long,
        val brevkode: String,
        val spraak: String,
        val avsenderEnhetsId: String,
        val saksbehandlerValg: T,
        val reserverForRedigering: Boolean,
    )

interface SaksbehandlerValgBrevdata