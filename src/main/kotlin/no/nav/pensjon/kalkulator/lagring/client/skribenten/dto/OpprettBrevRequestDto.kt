package no.nav.pensjon.kalkulator.lagring.client.skribenten.dto

import no.nav.pensjon.kalkulator.lagring.LagreSimulering

data class OpprettBrevRequestDto(
        val brevkode: String = "FFSF",
        val spraak: String = "NOB",
        val avsenderEnhetsId: EnhetId,
        val saksbehandlerValg: LagreSimulering,
        val reserverForRedigering: Boolean = false,
    )