package no.nav.pensjon.kalkulator.lagring

data class ForbeholdInnhold(
    val seksjoner: List<ForbeholdSeksjon>
)

data class Kortforbehold(
    val avsnitt: List<ForbeholdAvsnitt>
)

data class ForbeholdOgKortforbehold(
    val forbehold: ForbeholdInnhold?,
    val kortforbehold: Kortforbehold?
)

data class ForbeholdSeksjon(
    val tittel: String?,
    val avsnitt: List<ForbeholdAvsnitt>,
    /** Regelgrupper fra Sanity: ELLER mellom gruppene, OG innenfor en gruppe. Tom liste = alltid synlig. */
    val vilkaarGrupper: List<List<SanityVisningsvilkaar>>
) {
    fun erSynligMed(vilkaar: Collection<SanityVisningsvilkaar>): Boolean =
        vilkaarGrupper.isEmpty() || vilkaarGrupper.any { gruppe -> vilkaar.containsAll(gruppe) }
}

data class ForbeholdAvsnitt(
    val tekst: String,
    val punktliste: List<String>?
)

enum class SanityVisningsvilkaar(val externalValue: String) {
    BEREGNER_GAMMEL_AFP("beregnerGammelAfp"),
    BEREGNER_AFP_GENERELT("beregnerAfpUavhengigAvAarskull"),
    BEREGNER_AFP_PRIVAT("beregnerAfpPrivat"),
    BEREGNER_MED_GJENLEVENDERETT("beregnerMedGjenlevenderett"),
    HAR_UFOERETRYGD("harUfoeretrygd"),
    HAR_UTENLANDSOPPHOLD("harUtenlandsopphold"),
    HAR_VEDTAK_UTENLANDSOPPHOLD("harVedtakUtenlandsopphold"),
    HAR_GJENLEVENDE_ELLER_OMSTILLINGSSTOENAD("harGjenlevendeEllerOmstillingsstoenad");

    companion object {
        fun fromExternalValue(value: String?) =
            SanityVisningsvilkaar.entries.singleOrNull { it.externalValue.equals(value, true) }
    }
}