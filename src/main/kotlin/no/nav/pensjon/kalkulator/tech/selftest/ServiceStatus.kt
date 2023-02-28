package no.nav.pensjon.kalkulator.tech.selftest

enum class ServiceStatus(val code: Int, val color: String) {

    DOWN (0, "red"),
    UP(1, "green")
}
