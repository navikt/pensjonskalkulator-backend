package no.nav.pensjon.kalkulator.simulering.api.dto

data class SimuleringAlderDto(val aar: Int, val maaneder: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

data class SimuleringAlderV0Dto(val aar: Int, val maaned: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaned in 0..12) { "0 <= maaned <= 12" }
    }
}
