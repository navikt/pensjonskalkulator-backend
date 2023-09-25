package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand

data class PensjonsavtaleIngressSpecDto(
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeIngressSpecDto>,
    val antallInntektsaarEtterUttak: Int,
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int? = 0,
    val sivilstatus: Sivilstand? = null,
    val oenskesSimuleringAvFolketrygd: Boolean? = false
)

data class UttaksperiodeIngressSpecDto(
    val startAlder: Int,
    val startMaaned: Int,
    val grad: Int,
    val aarligInntekt: Int
)
