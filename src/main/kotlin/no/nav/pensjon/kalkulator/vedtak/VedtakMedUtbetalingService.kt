package no.nav.pensjon.kalkulator.vedtak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityCoroutineContext
import no.nav.pensjon.kalkulator.utbetaling.UtbetalingService
import org.springframework.stereotype.Service

@Service
class VedtakMedUtbetalingService(
    val loependeVedtakService: LoependeVedtakService,
    val utbetalingService: UtbetalingService
) {

    suspend fun hentVedtakMedUtbetaling(): LoependeVedtak {
        return withContext(Dispatchers.IO + SecurityCoroutineContext()) {

            val loependeVedtakDeferred =  async { loependeVedtakService.hentLoependeVedtak() }
            val sisteMaanedsUtbetalingDeferred =  async { utbetalingService.hentSisteMaanedsUtbetaling() }

            val loependeVedtak = loependeVedtakDeferred.await()
            val sisteMaanedsUtbetaling = sisteMaanedsUtbetalingDeferred.await()

            sisteMaanedsUtbetaling?.let {
                loependeVedtak.alderspensjon?.utbetalingSisteMaaned = UtbetalingSisteMaaned(
                    beloep = it.beloep,
                    posteringsdato = it.posteringsdato //brukere vil se utbetaling tigligst mulig uavhengig av utbetalingsstatus
                )
            }
            loependeVedtak
        }
    }
}