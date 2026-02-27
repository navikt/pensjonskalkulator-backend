package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

/**
 * Specifies impersonal parameters for simulering.
 * 'Impersonal' means parameters that are obtained without knowing the person's identity.
 */
data class ImpersonalSimuleringSpec(
    val simuleringType: SimuleringType,
    val sivilstand: Sivilstand? = null,
    val eps: EpsSpec,
    val forventetAarligInntektFoerUttak: Int? = null,
    val gradertUttak: GradertUttak? = null,
    val heltUttak: HeltUttak,
    val utenlandsopphold: Utenlandsopphold,
    val afpInntektMaanedFoerUttak: Boolean? = null,
    val afpOrdning: AfpOrdningType? = null,
    val innvilgetLivsvarigOffentligAfp: InnvilgetLivsvarigOffentligAfpSpec? = null,

    // For 'anonym simulering' only:
    val foedselAar: Int? = null,
    val inntektOver1GAntallAar: Int? = 0
)

/**
 * Informasjon om ektefelle/partner/samboer (EPS).
 */
data class EpsSpec(
    val levende: LevendeEps? = null,
    val avdoed: AvdoedEps? = null
)

data class LevendeEps(
    val harInntektOver2G: Boolean, // 2G = 2 ganger grunnbeløpet
    val harPensjon: Boolean
)

/**
 * Informasjon om avdød ektefelle/partner/samboer (EPS) er relevant for pensjon med gjenlevenderett.
 */
data class AvdoedEps(
    val pid: Pid,
    val doedsdato: LocalDate,
    val medlemAvFolketrygden: Boolean,
    val inntektFoerDoedBeloep: Int,
    val inntektErOverGrunnbeloepet: Boolean,
    val antallAarUtenlands: Int
)


data class Utenlandsopphold (
    val periodeListe: List<Opphold>,
    val antallAar: Int? = 0
)

data class Opphold (
    val fom: LocalDate,
    val tom: LocalDate?,
    val land: Land,
    val arbeidet: Boolean
)

/**
 * Spesifiserer egenskapene til en innvilget livsvarig AFP i offentlig sektor.
 */
data class InnvilgetLivsvarigOffentligAfpSpec(
    val aarligBruttoBeloep: Double,
    val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)
