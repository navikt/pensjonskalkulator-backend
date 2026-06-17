package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.Relasjonstype
import org.springframework.util.StringUtils.hasLength

/**
 * Ref. PSELV: PersonopplysningerActionDelegate.hentEpsRelasjonMedGjenlevenderettighet
 */
enum class RelasjonstypeDto(
    val internalValue: Relasjonstype,
    val correspondingSivilstatusListe: List<Sivilstatus> = emptyList()
) {
    EKTEFELLE(
        internalValue = Relasjonstype.EKTEFELLE,
        correspondingSivilstatusListe = listOf(
            Sivilstatus.GIFT,
            Sivilstatus.SEPARERT,
            Sivilstatus.SKILT,
            Sivilstatus.ENKE_ELLER_ENKEMANN
        )
    ),
    REGISTRERT_PARTNER(
        internalValue = Relasjonstype.REGISTRERT_PARTNER,
        correspondingSivilstatusListe = listOf(
            Sivilstatus.REGISTRERT_PARTNER,
            Sivilstatus.SEPARERT_PARTNER,
            Sivilstatus.SKILT_PARTNER,
            Sivilstatus.GJENLEVENDE_PARTNER
        )
    ),
    FRASKILT_EKTEFELLE(internalValue = Relasjonstype.FRASKILT_EKTEFELLE),
    FRASKILT_PARTNER(internalValue = Relasjonstype.FRASKILT_PARTNER),
    FRASEPARERT_EKTEFELLE(internalValue = Relasjonstype.FRASEPARERT_EKTEFELLE),
    FRASEPARERT_PARTNER(internalValue = Relasjonstype.FRASEPARERT_PARTNER),
    AVDOED_EKTEFELLE(internalValue = Relasjonstype.AVDOED_EKTEFELLE),
    AVDOED_PARTNER(internalValue = Relasjonstype.AVDOED_PARTNER),
    SAMBOER(
        internalValue = Relasjonstype.SAMBOER,
        correspondingSivilstatusListe = listOf(
            Sivilstatus.SAMBOER,
            Sivilstatus.UNKNOWN,
            Sivilstatus.UOPPGITT,
            Sivilstatus.UGIFT
        )
    ),
    BARN(internalValue = Relasjonstype.BARN),
    FAR(internalValue = Relasjonstype.FAR),
    MEDMOR(internalValue = Relasjonstype.MEDMOR),
    MOR(internalValue = Relasjonstype.MOR),
    HELSOESKEN(internalValue = Relasjonstype.HELSOESKEN),
    HALVSOESKEN_FELLES_MOR(internalValue = Relasjonstype.HALVSOESKEN_FELLES_MOR),
    HALVSOESKEN_FELLES_FAR_MEDMOR(internalValue = Relasjonstype.HALVSOESKEN_FELLES_FAR_MEDMOR),
    UKJENT(internalValue = Relasjonstype.UKJENT);

    companion object {
        private val log = KotlinLogging.logger {}

        fun fromSivilstatus(value: Sivilstatus?): RelasjonstypeDto =
            entries.firstOrNull { it.correspondingSivilstatusListe.contains(value) } ?: SAMBOER

        fun internalValue(value: String?): Relasjonstype =
            fromExternalValue(value).internalValue

        private fun fromExternalValue(value: String?): RelasjonstypeDto =
            RelasjonstypeDto.entries.firstOrNull { it.name.equals(value, true) } ?: default(value)

        private fun default(externalValue: String?): RelasjonstypeDto =
            if (hasLength(externalValue))
                UKJENT.also { log.warn { "Ukjent ekstern relasjonstype '$externalValue'" } }
            else
                UKJENT
    }
}