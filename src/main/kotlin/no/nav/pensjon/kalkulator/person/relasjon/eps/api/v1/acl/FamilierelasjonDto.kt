package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl

import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.EnumUtil.missingExternalValue
import java.time.LocalDate

data class FamilierelasjonDto(
    val pid: String?,
    val fom: LocalDate?,
    @field:NotNull val relasjonstype: RelasjonstypeDto,
    val relasjonPersondata: RelasjonPersondataDto?
)

data class RelasjonPersondataDto(
    val tilgangsbegrensning: TilgangsbegrensningDto?,
    val navn: NavnDto?,
    val foedselsdato: LocalDate?,
    val doedsdato: LocalDate?,
    val statsborgerskap: String?
)

data class NavnDto(
    val fornavn: String?,
    val mellomnavn: String?,
    val etternavn: String?
)

enum class RelasjonstypeDto(val internalValue: Relasjonstype) {
    EKTEFELLE(internalValue = Relasjonstype.EKTEFELLE),
    REGISTRERT_PARTNER(internalValue = Relasjonstype.REGISTRERT_PARTNER),
    SAMBOER(internalValue = Relasjonstype.SAMBOER);

    companion object {
        fun fromInternalValue(value: Relasjonstype?): RelasjonstypeDto =
            entries.firstOrNull { it.internalValue == value } ?: missingExternalValue(type = "relasjonstype", value)
    }
}

enum class TilgangsbegrensningDto(val internalValue: Tilgangsbegrensning) {
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