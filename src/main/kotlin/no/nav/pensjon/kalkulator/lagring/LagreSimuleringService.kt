package no.nav.pensjon.kalkulator.lagring

import no.nav.pensjon.kalkulator.lagring.client.LagreSimuleringClient
import no.nav.pensjon.kalkulator.sak.Sak
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType
import org.springframework.stereotype.Service

@Service
class LagreSimuleringService(
    private val sakService: SakService,
    private val client: LagreSimuleringClient
) {
    fun lagreSimulering(simulering: LagreSimulering): LagreSimuleringResponse {
        val sakstype = if (simulering.livsvarigOffentligAfpListe != null || simulering.tidsbegrensetOffentligAfp != null) SakType.AVTALEFESTET_PENSJON_I_OFFENTLIG_SEKTOR
        else if (simulering.privatAfpListe != null) SakType.AVTALEFESTET_PENSJON_I_PRIVAT_SEKTOR
        else SakType.ALDERSPENSJON

        val sakId = sakService.hentEllerOpprettSak(sakstype)
        return client.lagreSimulering(sakId, simulering)
    }
}
