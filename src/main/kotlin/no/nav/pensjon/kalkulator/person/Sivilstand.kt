package no.nav.pensjon.kalkulator.person

/**
 * Ref. lovdata.no/forskrift/2017-07-14-1201/§3-1-1
 */
enum class Sivilstand(
    val sivilstatus: Sivilstatus,
    val harEps: Boolean = false,
    val allowsSamboer: Boolean = true
) {
    UNKNOWN(sivilstatus = Sivilstatus.UNKNOWN),
    UOPPGITT(sivilstatus = Sivilstatus.UOPPGITT),
    UGIFT(sivilstatus = Sivilstatus.UGIFT),
    GIFT(sivilstatus = Sivilstatus.GIFT, harEps = true, allowsSamboer = false),
    ENKE_ELLER_ENKEMANN(sivilstatus = Sivilstatus.ENKE_ELLER_ENKEMANN),
    SKILT(sivilstatus = Sivilstatus.SKILT, allowsSamboer = false),
    SEPARERT(sivilstatus = Sivilstatus.SEPARERT, allowsSamboer = false),
    REGISTRERT_PARTNER(sivilstatus = Sivilstatus.REGISTRERT_PARTNER, harEps = true, allowsSamboer = false),
    SEPARERT_PARTNER(sivilstatus = Sivilstatus.SEPARERT_PARTNER, allowsSamboer = false),
    SKILT_PARTNER(sivilstatus = Sivilstatus.SKILT_PARTNER, allowsSamboer = false),
    GJENLEVENDE_PARTNER(sivilstatus = Sivilstatus.GJENLEVENDE_PARTNER),
    SAMBOER(sivilstatus = Sivilstatus.SAMBOER, harEps = true) //TODO remove
}

