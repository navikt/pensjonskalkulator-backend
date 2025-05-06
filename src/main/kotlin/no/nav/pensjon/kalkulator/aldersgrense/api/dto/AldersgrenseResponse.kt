package no.nav.pensjon.kalkulator.aldersgrense.api.dto

data class AldersgrenseResponse(
    val normertPensjoneringsalder: PersonAlder,
    val nedreAldersgrense: PersonAlder
)

data class PersonAlder(
    val aar: Int,
    val maaneder: Int
)
