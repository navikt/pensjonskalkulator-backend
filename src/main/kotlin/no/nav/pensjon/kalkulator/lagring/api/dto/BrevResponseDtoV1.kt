package no.nav.pensjon.kalkulator.lagring.api.dto

data class BrevResponseDtoV1(
    val info: BrevInfoResponseDtoV1,
)

data class BrevInfoResponseDtoV1(
    val id: String,
    val saksId: String,
)
