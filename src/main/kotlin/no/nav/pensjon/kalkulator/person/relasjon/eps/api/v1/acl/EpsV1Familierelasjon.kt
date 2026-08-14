package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.EnumUtil.missingExternalValue
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak
import java.time.LocalDate

/**
 * Using the prefix 'EpsV1' to avoid name clash with other DTOs (which causes problems in the generated Swagger API
 * documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
@JsonInclude(NON_NULL)
data class EpsV1Familierelasjon(
    val pid: String?,
    val fom: LocalDate?,
    @field:NotNull val relasjonstype: EpsV1Relasjonstype,
    val relasjonPersondata: EpsV1RelasjonPersondata?,
    val problem: EpsV1Problem? = null
)

@JsonInclude(NON_NULL)
data class EpsV1RelasjonPersondata(
    val tilgangsbegrensning: EpsV1Tilgangsbegrensning?,
    val navn: EpsV1Navn?,
    val foedselsdato: LocalDate?,
    val doedsdato: LocalDate?,
    val statsborgerskap: String?
)

@JsonInclude(NON_NULL)
data class EpsV1Navn(
    val fornavn: String?,
    val mellomnavn: String?,
    val etternavn: String?
)

@JsonInclude(NON_NULL)
data class EpsV1Problem(
    @field:Schema(description = "Type problem")
    @field:NotNull val type: EpsV1ProblemType,

    @field:Schema(description = "Beskrivelse av problemet")
    @field:NotNull val beskrivelse: String,

    @field:Schema(description = "Begrunnelse for nekting av tilgang til person")
    val tilgangsnekt: EpsV1Tilgangsnekt?
)

enum class EpsV1ProblemType {
    TILGANG_NEKTET,
    MANGELFULL_SPESIFIKASJON
}

data class EpsV1Tilgangsnekt(
    @field:Schema(description = "Årsakskode for nekting av tilgang til person")
    @field:NotNull
    val aarsak: EpsV1AvvisningAarsak,

    @field:Schema(description = "Begrunnelse for nekting av tilgang til person")
    @field:NotNull
    val begrunnelse: String
)

enum class EpsV1AvvisningAarsak(val internalValue: AvvisningAarsak) {

    STRENGT_FORTROLIG_ADRESSE(internalValue = AvvisningAarsak.STRENGT_FORTROLIG_ADRESSE),
    STRENGT_FORTROLIG_UTLAND(internalValue = AvvisningAarsak.STRENGT_FORTROLIG_UTLAND),
    AVDOED(internalValue = AvvisningAarsak.AVDOED),
    VERGEMAAL(internalValue = AvvisningAarsak.VERGEMAAL),
    PERSON_UTLAND(internalValue = AvvisningAarsak.PERSON_UTLAND),
    SKJERMING(internalValue = AvvisningAarsak.SKJERMING),
    FORTROLIG_ADRESSE(internalValue = AvvisningAarsak.FORTROLIG_ADRESSE),
    UKJENT_BOSTED(internalValue = AvvisningAarsak.UKJENT_BOSTED),
    GEOGRAFISK(internalValue = AvvisningAarsak.GEOGRAFISK),
    HABILITET(internalValue = AvvisningAarsak.HABILITET),
    POPULASJONSTILGANGSSJEKK_FEIL(internalValue = AvvisningAarsak.POPULASJONSTILGANGSSJEKK_FEIL),

    // Special value not used by tilgangsmaskinen (for handling unexpected/missing enum values):
    UNKNOWN(internalValue = AvvisningAarsak.UNKNOWN);

    // NB: These are not relevant for EPS access:
    // – MANGLENDE_FAGGRUPPE_MEDLEMSKAP
    // – TILGANGSSJEKK_FEIL

    companion object {
        val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: AvvisningAarsak?): EpsV1AvvisningAarsak =
            value?.let { valuesByInternal[it] } ?: missingExternalValue(type = "avvisningsårsak", value)
    }
}

enum class EpsV1Relasjonstype(val internalValue: Relasjonstype) {
    EKTEFELLE(internalValue = Relasjonstype.EKTEFELLE),
    REGISTRERT_PARTNER(internalValue = Relasjonstype.REGISTRERT_PARTNER),
    FRASKILT_EKTEFELLE(internalValue = Relasjonstype.FRASKILT_EKTEFELLE),
    FRASKILT_PARTNER(internalValue = Relasjonstype.FRASKILT_PARTNER),
    FRASEPARERT_EKTEFELLE(internalValue = Relasjonstype.FRASEPARERT_EKTEFELLE),
    FRASEPARERT_PARTNER(internalValue = Relasjonstype.FRASEPARERT_PARTNER),
    AVDOED_EKTEFELLE(internalValue = Relasjonstype.AVDOED_EKTEFELLE),
    AVDOED_PARTNER(internalValue = Relasjonstype.AVDOED_PARTNER),
    SAMBOER(internalValue = Relasjonstype.SAMBOER),
    BARN(internalValue = Relasjonstype.BARN),
    FAR(internalValue = Relasjonstype.FAR),
    MEDMOR(internalValue = Relasjonstype.MEDMOR),
    MOR(internalValue = Relasjonstype.MOR),
    HELSOESKEN(internalValue = Relasjonstype.HELSOESKEN),
    HALVSOESKEN_FELLES_MOR(internalValue = Relasjonstype.HALVSOESKEN_FELLES_MOR),
    HALVSOESKEN_FELLES_FAR_MEDMOR(internalValue = Relasjonstype.HALVSOESKEN_FELLES_FAR_MEDMOR),
    UKJENT(internalValue = Relasjonstype.UKJENT);

    companion object {
        val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: Relasjonstype?): EpsV1Relasjonstype =
            value?.let { valuesByInternal[it] } ?: missingExternalValue(type = "relasjonstype", value)
    }
}

enum class EpsV1Tilgangsbegrensning(val internalValue: Tilgangsbegrensning) {
    FORTROLIG(internalValue = Tilgangsbegrensning.FORTROLIG),
    STRENGT_FORTROLIG(internalValue = Tilgangsbegrensning.STRENGT_FORTROLIG),
    STRENGT_FORTROLIG_UTLAND(internalValue = Tilgangsbegrensning.STRENGT_FORTROLIG_UTLAND),
    UNKNOWN(internalValue = Tilgangsbegrensning.UNKNOWN);

    companion object {
        val valuesByInternal = entries.associateBy { it.internalValue }

        fun fromInternalValue(value: Tilgangsbegrensning?): EpsV1Tilgangsbegrensning =
            value?.let { valuesByInternal[it] } ?: missingExternalValue(type = "tilgangsbegrensning", value)
    }
}