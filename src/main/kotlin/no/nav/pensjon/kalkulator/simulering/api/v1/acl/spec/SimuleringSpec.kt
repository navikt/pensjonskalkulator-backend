package no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * DTO (data transfer object) som representerer spesifikasjon ("spec")
 * for simulering av pensjon.
 */
data class SimuleringV1Spec(
    @field:NotNull val simuleringstype: SimuleringV1SimuleringstypeSpec,
    val aarligInntektFoerUttakBeloep: Int? = null,
    val gradertUttak: SimuleringV1GradertUttakSpec? = null, // default is helt uttak (100 %)
    @field:NotNull val heltUttak: SimuleringV1HeltUttakSpec,
    val utenlandsperiodeListe: List<SimuleringV1UtenlandsperiodeSpec>? = null,
    val sivilstatus: CommonV1Sivilstatus? = null,
    val eps: SimuleringV1EpsSpec? = null,
    val offentligAfp: SimuleringV1OffentligAfpSpec? = null,
)

data class SimuleringV1GradertUttakSpec(
    @field:NotNull val grad: Int,
    @field:NotNull val uttaksalder: SimuleringV1AlderSpec,
    val aarligInntektVsaPensjonBeloep: Int? = null // Vsa = ved siden av
)

data class SimuleringV1HeltUttakSpec(
    @field:NotNull val uttaksalder: SimuleringV1AlderSpec,
    val aarligInntektVsaPensjon: SimuleringV1InntektSpec? = null
)

data class SimuleringV1InntektSpec(
    @field:NotNull val beloep: Int,
    @field:NotNull val sluttAlder: SimuleringV1AlderSpec
)

data class SimuleringV1UtenlandsperiodeSpec(
    @field:NotNull val fom: LocalDate,
    val tom: LocalDate? = null,
    @field:NotNull val landkode: String,
    @field:NotNull val arbeidetUtenlands: Boolean
)

/**
 * Informasjon om ektefelle/partner/samboer (EPS).
 */
data class SimuleringV1EpsSpec(
    val levende: SimuleringV1LevendeEps? = null,
    val avdoed: SimuleringV1AvdoedEps? = null
)

data class SimuleringV1LevendeEps(
    @field:NotNull val harInntektOver2G: Boolean, // 2G = 2 ganger grunnbeløpet
    @field:NotNull val harPensjon: Boolean
)

/**
 * Informasjon om avdød ektefelle/partner/samboer (EPS) er relevant for pensjon med gjenlevenderett.
 */
data class SimuleringV1AvdoedEps(
    @field:NotNull val pid: String,
    @field:NotNull val doedsdato: LocalDate,
    val medlemAvFolketrygden: Boolean? = null,
    val inntektFoerDoedBeloep: Int? = null,
    val inntektErOverGrunnbeloepet: Boolean? = null,
    val antallAarUtenlands: Int? = null
)

data class SimuleringV1OffentligAfpSpec(
    val harInntektMaanedenFoerUttak: Boolean? = null,
    val afpOrdning: SimuleringV1AfpOrdningTypeSpec? = null,
    val innvilgetLivsvarigAfpListe: List<SimuleringV1InnvilgetLivsvarigOffentligAfpSpec>? = null,
    // SERVICEBEREGNING_AFP-specific fields (required when simuleringstype = SERVICEBEREGNING_AFP, ignored otherwise)
    val inntektForrigeKalenderaar: Int? = null,
    val inntektFremTilUttak: Int? = null,
    val inntektMaanedFoerAfp: Int? = null
)

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
data class SimuleringV1InnvilgetLivsvarigOffentligAfpSpec(
    @field:NotNull val aarligBruttoBeloep: Double,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)

data class SimuleringV1AlderSpec(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
)

enum class SimuleringV1AfpOrdningTypeSpec(val internalValue: AfpOrdningType) {
    KOMMUNAL(internalValue = AfpOrdningType.AFPKOM),
    STATLIG(internalValue = AfpOrdningType.AFPSTAT),
    FINANSNAERINGEN(internalValue = AfpOrdningType.FINANS),
    KONVERTERT_PRIVAT(internalValue = AfpOrdningType.KONV_K),
    KONVERTERT_OFFENTLIG(internalValue = AfpOrdningType.KONV_O),
    LO_NHO_ORDNINGEN(internalValue = AfpOrdningType.LONHO),
    SPEKTER(internalValue = AfpOrdningType.NAVO),
}

enum class SimuleringV1SimuleringstypeSpec(val internalValue: SimuleringType) {
    ALDERSPENSJON(internalValue = SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP(internalValue = SimuleringType.PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON),
    ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT),
    ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ALDERSPENSJON_MED_GJENLEVENDERETT),
    ENDRING_ALDERSPENSJON(internalValue = SimuleringType.ENDRING_ALDERSPENSJON),
    ENDRING_ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ENDRING_ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT),
    ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT),
    SERVICEBEREGN_AFP(internalValue = SimuleringType.SERVICEBEREGN_AFP)
    // ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP har ingen tilsvarende type for endring (støttes ikke)
}