package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

data class OpprettBrevRequestDtoV1<out T : StatiskFagsystemBrevdata>(
        val saksId: Long,
        val brevkode: String,
        val spraak: String,
        val avsenderEnhetsId: String,
        val statiskFagsystemBrevdata: T,
        val saksbehandlerValg: SaksbehandlerValgDtoV1,
        val reserverForRedigering: Boolean,
    )

interface StatiskFagsystemBrevdata

data class SaksbehandlerValgDtoV1(
    val ingenYtelser: Boolean = true,
)