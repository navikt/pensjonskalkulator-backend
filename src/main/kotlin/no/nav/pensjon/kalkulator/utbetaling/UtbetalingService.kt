package no.nav.pensjon.kalkulator.utbetaling

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.utbetaling.client.UtbetalingClient
import org.springframework.stereotype.Component

@Component
class UtbetalingService(
    private val pidGetter: PidGetter,
    val utbetalingClient: UtbetalingClient
) {
    private val log = KotlinLogging.logger {}

    suspend fun hentSisteMaanedsUtbetaling(): Utbetaling? {
        val utbetalinger: List<Utbetaling> = utbetalingClient.hentSisteMaanedsUtbetaling(pidGetter.pid())
        log.info { "Hentet utbetalinger: $utbetalinger" }
        return utbetalinger
            .filter { it.gjelderAlderspensjon }
            .filter { erMaanedsUtbetaling(it) }
            .maxByOrNull { it.posteringsdato }
    }

    companion object {
        fun erMaanedsUtbetaling(utbetaling: Utbetaling) =
            utbetaling.fom.plusMonths(1).minusDays(1) == utbetaling.tom
    }
}