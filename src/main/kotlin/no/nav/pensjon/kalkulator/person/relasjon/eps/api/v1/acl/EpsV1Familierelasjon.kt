package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.EnumUtil.missingExternalValue
import java.time.LocalDate

/**
 * Using the prefix 'EpsV1' to avoid name clash with other DTOs (which causes problems in the generated Swagger API
 * documentation).
 * An alternative is to use 'springdoc.use-fqn=true', but this causes problems for the frontend's type checker (which
 * cannot handle DTO names with dots).
 */
data class EpsV1Familierelasjon(
    val pid: String?,
    val fom: LocalDate?,
    @field:NotNull val relasjonstype: EpsV1Relasjonstype,
    val relasjonPersondata: EpsV1RelasjonPersondata?
)

data class EpsV1RelasjonPersondata(
    val tilgangsbegrensning: EpsV1Tilgangsbegrensning?,
    val navn: EpsV1Navn?,
    val foedselsdato: LocalDate?,
    val doedsdato: LocalDate?,
    val statsborgerskap: String?
)

data class EpsV1Navn(
    val fornavn: String?,
    val mellomnavn: String?,
    val etternavn: String?
)

enum class EpsV1Relasjonstype(val internalValue: Relasjonstype) {
    EKTEFELLE(internalValue = Relasjonstype.EKTEFELLE),
    REGISTRERT_PARTNER(internalValue = Relasjonstype.REGISTRERT_PARTNER),
    SAMBOER(internalValue = Relasjonstype.SAMBOER);

    companion object {
        fun fromInternalValue(value: Relasjonstype?): EpsV1Relasjonstype =
            entries.firstOrNull { it.internalValue == value } ?: missingExternalValue(type = "relasjonstype", value)
    }
}

enum class EpsV1Tilgangsbegrensning(val internalValue: Tilgangsbegrensning) {
    FORTROLIG(internalValue = Tilgangsbegrensning.FORTROLIG),
    STRENGT_FORTROLIG(internalValue = Tilgangsbegrensning.STRENGT_FORTROLIG),
    STRENGT_FORTROLIG_UTLAND(internalValue = Tilgangsbegrensning.STRENGT_FORTROLIG_UTLAND),
    UNKNOWN(internalValue = Tilgangsbegrensning.UNKNOWN);

    companion object {
        fun fromInternalValue(value: Tilgangsbegrensning?) =
            entries.firstOrNull { it.internalValue == value }
                ?: missingExternalValue(type = "tilgangsbegrensning", value)
    }
}