package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand

data class PensjonsavtaleSpec(
    val pid: Pid,
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpec>,
    val antallInntektsaarEtterUttak: Int,
    val harAfp: Boolean = false,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val antallAarIUtlandetEtter16: Int = 0,
    val sivilstatus: Sivilstand? = null,
    val oenskesSimuleringAvFolketrygd: Boolean = false
)

data class UttaksperiodeSpec(
    val start: Alder,
    val grad: Uttaksgrad,
    val aarligInntekt: Int
)
