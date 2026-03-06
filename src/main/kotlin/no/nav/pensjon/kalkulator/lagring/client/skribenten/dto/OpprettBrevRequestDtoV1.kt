package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

data class OpprettBrevRequestDtoV1(
        val brevkode: String,
        val spraak: String,
        val avsenderEnhetsId: String,
        val saksbehandlerValg: SimuleringBrevDtoV1,
        val reserverForRedigering: Boolean,
    )