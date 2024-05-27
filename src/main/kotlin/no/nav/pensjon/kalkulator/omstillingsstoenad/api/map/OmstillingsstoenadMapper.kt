package no.nav.pensjon.kalkulator.omstillingsstoenad.api.map

import no.nav.pensjon.kalkulator.omstillingsstoenad.api.dto.BrukerHarLoependeOmstillingsstoenadEllerGjenlevendeYtelse

object OmstillingsstoenadMapper {

    fun toDto(mottarStoenad: Boolean) = BrukerHarLoependeOmstillingsstoenadEllerGjenlevendeYtelse(mottarStoenad)
}