package no.nav.pensjon.kalkulator.aldersgrense.api.dto

data class AldersgrenseResultV1(
    val normertPensjoneringsalder: PersonAlder,
    val nedreAldersgrense: PersonAlder
)

data class PersonAlder(
    val aar: Int,
    val maaneder: Int
)
