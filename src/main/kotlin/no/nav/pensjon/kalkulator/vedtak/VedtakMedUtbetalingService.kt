package no.nav.pensjon.kalkulator.vedtak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.utbetaling.SamletUtbetaling
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingService
import org.springframework.stereotype.Service

@Service
class VedtakMedUtbetalingService(
    private val vedtakService: LoependeVedtakService,
    private val utbetalingService: UtbetalingService,
    private val innledningService: InnledningService
) {
    suspend fun hentVedtakMedUtbetaling(): VedtakSamling =
        withContext(Dispatchers.IO + SecurityCoroutineContext()) {
            val vedtakDeferred = async { vedtakService.hentLoependeVedtak() }
            val utbetalingDeferred = async { utbetalingService.hentSisteMaanedsUtbetaling() }
            val innledningDeferred = async { innledningService.hentInnledningsdata() }

            val sisteMaanedsUtbetaling: SamletUtbetaling? = utbetalingDeferred.await()
            val innledningsdata: Innledningsdata = innledningDeferred.await()

            val vedtakSamling: VedtakSamling =
                vedtakDeferred.await().withGjenlevenderett(innledningsdata.harGjenlevenderett)

            sisteMaanedsUtbetaling?.let {
                vedtakSamling.withAlderspensjonUtbetalingSisteMaaned(
                    Utbetaling(
                        beloep = it.totalBeloep,
                        posteringsdato = it.posteringsdato // brukere vil se utbetaling tidligst mulig uavhengig av utbetalingsstatus
                    )
                )
            } ?: vedtakSamling
        }
}
