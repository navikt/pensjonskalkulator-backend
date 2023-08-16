package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp

/**
 * Ordning-type in the 'tp' service is same as avdelingstype in the TSS service
 */
enum class TpOrdningType(val externalValue: String) {
    OFFENTLIG_TJENESTEPENSJONSORDNING("TPOF"),
    PRIVAT_TJENESTEPENSJONSLEVERANDOER("TPPR"),
    TJENESTEPENSJON_UTLAND("TPUT")
}
