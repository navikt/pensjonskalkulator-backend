package no.nav.pensjon.kalkulator.lagring.api.dto

data class LagreSimuleringResponseDtoV1(
    val brevId: String,
    val sakId: String,
    val url: String, //Denne vil forenkle testing i dev
)
