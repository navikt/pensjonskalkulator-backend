package no.nav.pensjon.kalkulator.uttaksalder

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Uttaksalder(val aar: Int, val maaned: Int)
