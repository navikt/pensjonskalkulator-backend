package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.PersonaliaType
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Tilgangsbegrensning
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import org.springframework.util.StringUtils.hasLength
import java.time.LocalDate

data class FamilierelasjonDto(
    val pid: String?,
    val fom: LocalDate?,
    val relasjonstype: RelasjonstypeDto,
    val relasjonPersondata: RelasjonPersondataDto?
)

/**
 * Ref. PSELV: PersonopplysningerActionDelegate.hentEpsRelasjonMedGjenlevenderettighet
 */
enum class RelasjonstypeDto(
    val internalValue: Relasjonstype,
    val correspondingSivilstatusListe: List<Sivilstand>
) {
    EKTEFELLE(
        internalValue = Relasjonstype.EKTEFELLE,
        correspondingSivilstatusListe = listOf(
            Sivilstand.GIFT,
            Sivilstand.SEPARERT,
            Sivilstand.SKILT,
            Sivilstand.ENKE_ELLER_ENKEMANN
        )
    ),
    REGISTRERT_PARTNER(
        internalValue = Relasjonstype.REGISTRERT_PARTNER,
        correspondingSivilstatusListe = listOf(
            Sivilstand.REGISTRERT_PARTNER,
            Sivilstand.SEPARERT_PARTNER,
            Sivilstand.SKILT_PARTNER,
            Sivilstand.GJENLEVENDE_PARTNER
        )
    ),
    SAMBOER(
        internalValue = Relasjonstype.SAMBOER,
        correspondingSivilstatusListe = listOf(
            Sivilstand.SAMBOER,
            Sivilstand.UNKNOWN,
            Sivilstand.UOPPGITT,
            Sivilstand.UGIFT
        )
    );

    companion object {
        fun fromInternalValue(value: Relasjonstype?): RelasjonstypeDto =
            entries.firstOrNull { it.internalValue == value }
                ?: throw IllegalArgumentException("Ingen ekstern verdi for relasjonstype $value")

        fun fromSivilstatus(value: Sivilstand?): RelasjonstypeDto =
            entries.firstOrNull { it.correspondingSivilstatusListe.contains(value) } ?: SAMBOER
    }
}

data class RelasjonPersondataDto(
    val tilgangsbegrensning: String?,
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

enum class PersonaliaTypeDto(val internalValue: PersonaliaType, val externalValue: String) {
    NAVN(internalValue = PersonaliaType.NAVN, externalValue = "navn"),
    FOEDSELSDATO(internalValue = PersonaliaType.FOEDSELSDATO, externalValue = "foedselsdato"),
    DOEDSDATO(internalValue = PersonaliaType.DOEDSDATO, externalValue = "doedsdato"),
    STATSBORGERSKAP(internalValue = PersonaliaType.STATSBORGERSKAP, externalValue = "statsborgerskap");

    companion object {
        fun externalValue(value: PersonaliaType?): String =
            fromInternalValue(value).externalValue

        private fun fromInternalValue(value: PersonaliaType?): PersonaliaTypeDto =
            entries.firstOrNull { it.internalValue == value }
                ?: throw RuntimeException("Ugyldig personaliatype: $value")
    }
}

enum class TilgangsbegrensningDto(val internalValue: Tilgangsbegrensning) {
    FORTROLIG(internalValue = Tilgangsbegrensning.FORTROLIG),
    STRENGT_FORTROLIG(internalValue = Tilgangsbegrensning.STRENGT_FORTROLIG),
    STRENGT_FORTROLIG_UTLAND(internalValue = Tilgangsbegrensning.STRENGT_FORTROLIG_UTLAND),
    UNKNOWN(internalValue = Tilgangsbegrensning.UNKNOWN);

    companion object {
        private val log = KotlinLogging.logger {}

        fun internalValue(value: String?): Tilgangsbegrensning =
            fromExternalValue(value).internalValue

        private fun fromExternalValue(value: String?): TilgangsbegrensningDto =
            entries.firstOrNull { it.name.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?): TilgangsbegrensningDto =
            if (hasLength(externalValue))
                UNKNOWN.also { log.warn { "Ukjent ekstern tilgangsbegrensning '$externalValue'" } }
            else
                UNKNOWN
    }
}