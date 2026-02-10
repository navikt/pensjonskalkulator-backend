package no.nav.pensjon.kalkulator.simulering.api.intern.v1.acl.spec

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

/**
 * DTO (data transfer object) som representerer spesifikasjon ("spec")
 * for simulering av pensjon i Nav-intern kontekst.
 */
data class SimuleringSpecDto(
    @field:NotNull val simuleringstype: SimuleringstypeSpecDto,
    val aarligInntektFoerUttakBeloep: Int?,
    val gradertUttak: GradertUttakSpecDto? = null, // default is helt uttak (100 %)
    @field:NotNull val heltUttak: HeltUttakSpecDto,
    val utenlandsperiodeListe: List<UtenlandsperiodeSpecDto>? = null,
    val sivilstatus: SivilstatusSpecDto? = null,
    val eps: EpsSpecDto? = null,
    val offentligAfp: OffentligAfpSpecDto? = null
)

data class GradertUttakSpecDto(
    @field:NotNull val grad: Int,
    @field:NotNull val uttaksalder: AlderSpecDto,
    val aarligInntektVsaPensjonBeloep: Int? = null // Vsa = ved siden av
)

data class HeltUttakSpecDto(
    @field:NotNull val uttaksalder: AlderSpecDto,
    val aarligInntektVsaPensjon: InntektSpecDto? = null
)

data class InntektSpecDto(
    @field:NotNull val beloep: Int,
    @field:NotNull val sluttAlder: AlderSpecDto
)

data class UtenlandsperiodeSpecDto(
    @field:NotNull val fom: LocalDate,
    val tom: LocalDate? = null,
    @field:NotNull val landkode: String,
    @field:NotNull val arbeidetUtenlands: Boolean
)

/**
 * Informasjon om ektefelle/partner/samboer (EPS).
 */
data class EpsSpecDto(
    @field:NotNull val levende: LevendeEpsDto? = null,
    @field:NotNull val avdoed: AvdoedEpsDto? = null
)

data class LevendeEpsDto(
    @field:NotNull val harInntektOver2G: Boolean, // 2G = 2 ganger grunnbeløpet
    @field:NotNull val harPensjon: Boolean
)

/**
 * Informasjon om avdød ektefelle/partner/samboer (EPS) er relevant for pensjon med gjenlevenderett.
 */
data class AvdoedEpsDto(
    @field:NotNull val pid: String,
    @field:NotNull val doedsdato: LocalDate,
    val medlemAvFolketrygden: Boolean? = false,
    val inntektFoerDoedBeloep: Int? = 0,
    val inntektErOverGrunnbeloepet: Boolean? = false,
    val antallAarUtenlands: Int? = 0
)

data class OffentligAfpSpecDto(
    val harInntektMaanedenFoerUttak: Boolean? = null,
    val afpOrdning: AfpOrdningTypeSpecDto? = null,
    val innvilgetLivsvarigAfpListe: List<InnvilgetLivsvarigOffentligAfpSpecDto>? = null
)

/**
 * Spesifiserer egenskapene til en løpende livsvarig AFP i offentlig sektor.
 */
data class InnvilgetLivsvarigOffentligAfpSpecDto(
    @field:NotNull val aarligBruttoBeloep: Double,
    @field:NotNull @param:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd") val uttakFom: LocalDate,
    val sistRegulertGrunnbeloep: Int? = null
)

data class AlderSpecDto(
    @field:NotNull val aar: Int,
    @field:NotNull val maaneder: Int
) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaneder in 0..11) { "0 <= maaneder <= 11" }
    }
}

enum class AfpOrdningTypeSpecDto(val internalValue: AfpOrdningType) {
    KOMMUNAL(internalValue = AfpOrdningType.AFPKOM),
    STATLIG(internalValue = AfpOrdningType.AFPSTAT),
    FINANSNAERINGEN(internalValue = AfpOrdningType.FINANS),
    KONVERTERT_PRIVAT(internalValue = AfpOrdningType.KONV_K),
    KONVERTERT_OFFENTLIG(internalValue = AfpOrdningType.KONV_O),
    LO_NHO_ORDNINGEN(internalValue = AfpOrdningType.LONHO),
    SPEKTER(internalValue = AfpOrdningType.NAVO),
}

enum class SimuleringstypeSpecDto(val internalValue: SimuleringType) {
    ALDERSPENSJON(internalValue = SimuleringType.ALDERSPENSJON),
    ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP(internalValue = SimuleringType.PRE2025_OFFENTLIG_AFP_ETTERFULGT_AV_ALDERSPENSJON),
    ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT),
    ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ALDERSPENSJON_MED_GJENLEVENDERETT),
    ENDRING_ALDERSPENSJON(internalValue = SimuleringType.ENDRING_ALDERSPENSJON),
    ENDRING_ALDERSPENSJON_MED_LIVSVARIG_OFFENTLIG_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG),
    ENDRING_ALDERSPENSJON_MED_PRIVAT_AFP(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_AFP_PRIVAT),
    ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT(internalValue = SimuleringType.ENDRING_ALDERSPENSJON_MED_GJENLEVENDERETT)
    // ALDERSPENSJON_MED_TIDSBEGRENSET_OFFENTLIG_AFP har ingen tilsvarende type for endring (støttes ikke)
}

enum class SivilstatusSpecDto(val internalValue: Sivilstand = Sivilstand.UOPPGITT) {
    UOPPGITT,
    UGIFT(internalValue = Sivilstand.UGIFT),
    GIFT(internalValue = Sivilstand.GIFT),
    ENKE_ELLER_ENKEMANN(internalValue = Sivilstand.ENKE_ELLER_ENKEMANN),
    SKILT(internalValue = Sivilstand.SKILT),
    SEPARERT(internalValue = Sivilstand.SEPARERT),
    REGISTRERT_PARTNER(internalValue = Sivilstand.REGISTRERT_PARTNER),
    SEPARERT_PARTNER(internalValue = Sivilstand.SEPARERT_PARTNER),
    SKILT_PARTNER(internalValue = Sivilstand.SKILT_PARTNER),
    GJENLEVENDE_PARTNER(internalValue = Sivilstand.GJENLEVENDE_PARTNER),
    SAMBOER(internalValue = Sivilstand.SAMBOER)
}
