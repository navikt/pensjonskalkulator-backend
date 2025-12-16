package no.nav.pensjon.kalkulator.omstillingsstoenad.api.dto

import jakarta.validation.constraints.NotNull

data class BrukerHarLoependeOmstillingsstoenadEllerGjenlevendeYtelse(
    @field:NotNull val harLoependeSak: Boolean
)
