package no.nav.pensjon.kalkulator.aldersgrense.api.dto

data class AldersgrenseResultV2(
    val normertPensjoneringsalder: PersonAlderV2,
    val nedreAldersgrense: PersonAlderV2,
    val oevreAldersgrense: PersonAlderV2,
)

data class PersonAlderV2(
    val aar: Int,
    val maaneder: Int
)
