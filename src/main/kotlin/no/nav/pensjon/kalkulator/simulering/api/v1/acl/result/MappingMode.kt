package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

/**
 * Defines what information to map in various contexts.
 */
enum class MappingMode(
    val reduced: Boolean = false,
    val extended: Boolean = false
) {
    INTERNAL,
    NORMAL_EXTERNAL(reduced = true),
    EXTENDED_EXTERNAL(extended = true)
}