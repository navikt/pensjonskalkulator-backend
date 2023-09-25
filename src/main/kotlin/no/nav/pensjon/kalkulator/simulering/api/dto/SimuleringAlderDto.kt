package no.nav.pensjon.kalkulator.simulering.api.dto

data class SimuleringAlderDto(val aar: Int, val maaned: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaned in 0..12) { "0 <= maaned <= 12" }
    }
}
