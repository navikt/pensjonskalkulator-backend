package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand

data class PensjonsavtaleSpec(
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpec>,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val sivilstand: Sivilstand? = null
)

data class UttaksperiodeSpec(
    val startAlder: Alder,
    val grad: Uttaksgrad,
    val aarligInntekt: InntektSpec?,
)

data class InntektSpec(
    val aarligBeloep: Int,
    val tomAlder: Alder? = null
)
