package no.nav.pensjon.kalkulator.person

/**
 * For pdlCode definitions see: https://pdldocs-navno.msappproxy.net/ekstern/index.html#_sivilstand
 */
enum class Sivilstand(val pdlCode: String) {

    UOPPGITT("UOPPGITT"),
    UGIFT("UGIFT"),
    GIFT("GIFT"),
    ENKE_ELLER_ENKEMANN("ENKE_ELLER_ENKEMANN"),
    SKILT("SKILT"),
    SEPARERT("SEPARERT"),
    REGISTRERT_PARTNER("REGISTRERT_PARTNER"),
    SEPARERT_PARTNER("SEPARERT_PARTNER"),
    SKILT_PARTNER("SKILT_PARTNER"),
    GJENLEVENDE_PARTNER("GJENLEVENDE_PARTNER");

   companion object {
       fun forPdlCode(code: String): Sivilstand =
            Sivilstand.values().firstOrNull { it.pdlCode == code } ?: UOPPGITT
    }
}
