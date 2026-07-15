package no.nav.pensjon.kalkulator.tech.representasjon

import no.nav.pensjon.kalkulator.person.Pid

data class Representasjon(
    val isValid: Boolean,
    val fullmaktsgiver: Personalia?
)

data class Personalia(
    val navn: String,
    val pid: Pid
)