package no.nav.pensjon.kalkulator.person

/**
 * For pdlCode definitions see: https://pdldocs-navno.msappproxy.net/ekstern/index.html#_sivilstand
 * For reglerCode definitions see:
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-java/src/main/java/no/nav/domain/pensjon/kjerne/kodetabeller/SivilstandTypeCode.java
 */
enum class Sivilstand(val pdlCode: String, val reglerCode: String) {

    UGIFT("UGIFT", "UGIF"),
    OTHER("OTHER", "NULL");

    companion object {
        fun forPdlCode(code: String): Sivilstand =
            Sivilstand.values().firstOrNull { it.pdlCode == code } ?: OTHER
    }
}
