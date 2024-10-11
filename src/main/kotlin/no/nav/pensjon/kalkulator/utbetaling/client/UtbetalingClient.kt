package no.nav.pensjon.kalkulator.utbetaling.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling

interface UtbetalingClient {

    suspend fun hentSisteMaanedsUtbetaling(pid: Pid): List<Utbetaling>
}