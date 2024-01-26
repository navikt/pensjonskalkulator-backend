package no.nav.pensjon.kalkulator.ekskludering

import no.nav.pensjon.kalkulator.sak.RelevantSakStatus
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import org.springframework.stereotype.Service

@Service
class EkskluderingFacade(
    val sakService: SakService,
    val tjenestepensjonService: TjenestepensjonService
) {
    fun erEkskludert(): EkskluderingStatus =
        ekskluderingStatus(sakService.sakStatus()) ?: annenEkskluderingStatus()

    private fun ekskluderingStatus(sakStatus: RelevantSakStatus): EkskluderingStatus? =
        if (sakStatus.harSak)
            EkskluderingStatus(true, EkskluderingAarsak.from(sakStatus.sakType))
        else
            null

    private fun annenEkskluderingStatus() =
        if (tjenestepensjonService.erApoteker())
            EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        else
            EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)
}
