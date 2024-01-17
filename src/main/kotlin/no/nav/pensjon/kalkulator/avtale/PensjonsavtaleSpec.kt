package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand

data class PensjonsavtaleSpec(
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpec>,
    val antallInntektsaarEtterUttak: Int? = null, // deprecated - supports V1 only
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int = 0,
    val sivilstand: Sivilstand? = null
)

data class UttaksperiodeSpec(
    val startAlder: Alder,
    val grad: Uttaksgrad,
    val aarligInntekt: InntektSpec,
)

data class InntektSpec(
    val aarligBeloep: Int,
    val tomAlder: Alder? = null
)
