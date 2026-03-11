package no.nav.pensjon.kalkulator.simulering.api.v1.acl.result

/**
 * Defines what information to map in various contexts.
 */
enum class MappingMode(
    val mapGjenlevendetillegg: Boolean = true,
    val extended: Boolean = false
) {
    INTERNAL,
    NORMAL_EXTERNAL(mapGjenlevendetillegg = false),
    EXTENDED_EXTERNAL(extended = true)
}