package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.fortrolig.FortroligAdresseService
import no.nav.pensjon.kalkulator.person.Fortrolighet
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.SkjermingService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Sjekker innlogget brukers tilgang basert på:
 * - tilhørighet til grupper med grunntilgang (grunnroller)
 * - skjerming, dvs. tilleggstilgang til egne ansatte
 * - adressebeskyttelse, dvs. tilleggstilgang til beskyttede personer (fortrolig, strengt fortrolig, utland)
 * Ref.: https://navno.sharepoint.com/sites/fag-og-ytelser-pesys/SitePages/Tilgangsstyring-i-Pesys.aspx
 */
@Service
class GroupMembershipService(
    @Value("\${group-id.saksbehandler}") private val saksbehandlerGroupId: String,
    @Value("\${group-id.egne-ansatte}") private val egneAnsatteGroupId: String,
    @Value("\${group-id.fortrolig-adresse}") private val fortroligAdresseGroupId: String,
    @Value("\${group-id.strengt-fortrolig-adresse}") private val strengtFortroligAdresseGroupId: String,
    private val groupService: GroupService,
    private val skjermingService: SkjermingService,
    private val adresseService: FortroligAdresseService
) {
    fun innloggetBrukerHarTilgang(pid: Pid): Boolean {
        val groups = groupService.groups()

        return basisTilgangSjekkOk(groups) &&
                egenAnsattTilgangSjekkOk(groups, pid) &&
                adressebeskyttetPersonSjekkOk(groups, pid)
    }

    private fun basisTilgangSjekkOk(groups: List<String>): Boolean = groups.contains(saksbehandlerGroupId)

    private fun egenAnsattTilgangSjekkOk(groups: List<String>, pid: Pid): Boolean =
        groups.contains(egneAnsatteGroupId) || skjermingService.personErTilgjengelig(pid)

    private fun adressebeskyttetPersonSjekkOk(groups: List<String>, pid: Pid): Boolean {
        with(adresseService.adressebeskyttelseGradering(pid)) {
            return fortrolighet == Fortrolighet.INGEN
                    || fortrolighet == Fortrolighet.MILD && groups.contains(fortroligAdresseGroupId)
                    || fortrolighet == Fortrolighet.STRENG && groups.contains(strengtFortroligAdresseGroupId)
        }
    }
}
