package no.nav.pensjon.kalkulator.tech.trace

fun interface CallIdGenerator {
    fun newId(): String
}
