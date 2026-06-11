package no.nav.pensjon.kalkulator.lagring

import no.nav.pensjon.kalkulator.lagring.client.LagreSimuleringClient
import no.nav.pensjon.kalkulator.lagring.client.sanity.ForbeholdClient
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType
import org.springframework.stereotype.Service

@Service
class LagreSimuleringService(
    private val sakService: SakService,
    private val client: LagreSimuleringClient,
    private val forbeholdClient: ForbeholdClient
) {
    fun lagreSimulering(simulering: LagreSimulering): LagreSimuleringResponse {
        val sakstype = if (simulering.afpOffentligLivsvarig != null || simulering.afpOffentligTidsbegrenset != null) SakType.AVTALEFESTET_PENSJON_I_OFFENTLIG_SEKTOR
        else if (simulering.afpPrivat != null) SakType.AVTALEFESTET_PENSJON_I_PRIVAT_SEKTOR
        else SakType.ALDERSPENSJON

        val sakId = sakService.hentEllerOpprettSak(sakstype)
        val forbehold = forbeholdClient.fetchForbehold()
        val filteredForbehold = forbehold?.seksjoner?.filter { seksjon ->
            seksjon.vilkaarsliste.all { vilkaar ->
                simulering.simuleringsinformasjon?.sanityVisningsvilkaar?.contains(vilkaar) == true
            }
        }

        return client.lagreSimulering(sakId, simulering, ForbeholdInnhold(filteredForbehold ?: emptyList()))
    }
}
