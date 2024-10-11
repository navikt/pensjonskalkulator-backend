package no.nav.pensjon.kalkulator.utbetaling

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.utbetaling.client.UtbetalingClient
import org.springframework.stereotype.Component

@Component
class UtbetalingService(
    private val pidGetter: PidGetter,
    val utbetalingClient: UtbetalingClient) {

    suspend fun hentSisteMaanedsUtbetaling(): Utbetaling? {
        return utbetalingClient.hentSisteMaanedsUtbetaling(pidGetter.pid())
            .filter { it.gjelderAlderspensjon }
            .maxByOrNull { it.posteringsdato }
    }
}