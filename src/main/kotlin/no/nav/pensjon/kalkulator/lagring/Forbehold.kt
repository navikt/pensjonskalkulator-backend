package no.nav.pensjon.kalkulator.lagring

data class ForbeholdInnhold(
    val seksjoner: List<ForbeholdSeksjon>
)

data class ForbeholdSeksjon(
    val tittel: String?,
    val avsnitt: List<ForbeholdAvsnitt>,
    val vilkaarsliste: List<SanityVisningsvilkaar>
)

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
    HAR_GJENLEVENDE_ELLER_OMSTILLINGSSTOENAD("harGjenlevendeEllerOmstillingsstoenad");

    companion object {
        fun fromExternalValue(value: String?) =
            SanityVisningsvilkaar.entries.singleOrNull { it.externalValue.equals(value, true) }
    }
}