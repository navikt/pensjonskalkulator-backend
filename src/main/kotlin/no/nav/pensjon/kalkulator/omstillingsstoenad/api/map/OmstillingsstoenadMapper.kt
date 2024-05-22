package no.nav.pensjon.kalkulator.omstillingsstoenad.api.map

import no.nav.pensjon.kalkulator.omstillingsstoenad.api.dto.BrukerMottarOmstillingsstoenad

object OmstillingsstoenadMapper {

    fun toDto(mottarStoenad: Boolean) = BrukerMottarOmstillingsstoenad(mottarStoenad)
}