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
    fun ekskluderingPgaSakEllerApoteker(): EkskluderingStatus =
        sakEkskludering(sakStatus = sakService.sakStatus()) ?: apotekerEkskludering()

    fun apotekerEkskludering(): EkskluderingStatus =
        if (tjenestepensjonService.erApoteker())
            EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.ER_APOTEKER)
        else
            EkskluderingStatus(ekskludert = false, aarsak = EkskluderingAarsak.NONE)

    private companion object {

        private fun sakEkskludering(sakStatus: RelevantSakStatus): EkskluderingStatus? =
            if (sakStatus.harSak)
                EkskluderingStatus(ekskludert = true, aarsak = EkskluderingAarsak.from(sakStatus.sakType))
            else
                null
    }
}
