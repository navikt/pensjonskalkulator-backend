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

    suspend fun hentSisteMaanedsUtbetaling(): SamletUtbetaling? {
        val utbetalinger: List<Utbetaling> = utbetalingClient.hentSisteMaanedsUtbetaling(pidGetter.pid())
        log.info { "Hentet utbetalinger: $utbetalinger" }

        val alderspensjonUtbetalinger = utbetalinger.filter { it.gjelderAlderspensjon && it.beloep != null }

        if (alderspensjonUtbetalinger.isEmpty()) {
            return null
        }

        return SamletUtbetaling(
            totalBeloep = alderspensjonUtbetalinger.sumOf { it.beloep!! },
            posteringsdato = alderspensjonUtbetalinger.maxOf { it.posteringsdato }
        )
    }

}