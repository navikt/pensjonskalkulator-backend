package no.nav.pensjon.kalkulator.avtale.api.dto

import no.nav.pensjon.kalkulator.avtale.client.np.Sivilstatus

data class PensjonsavtaleSpecDto(
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpecDto>,
    val antallInntektsaarEtterUttak: Int,
    val harAfp: Boolean? = false,
    val harEpsPensjon: Boolean? = true, // Norsk Pensjon default
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = true, // Norsk Pensjon default
    val antallAarIUtlandetEtter16: Int? = 0,
    val sivilstatus: Sivilstatus? = Sivilstatus.GIFT, // Norsk Pensjon default
    val oenskesSimuleringAvFolketrygd: Boolean? = false
)

data class UttaksperiodeSpecDto(
    val startAlder: Int,
    val startMaaned: Int,
    val grad: Int,
    val aarligInntekt: Int
)
