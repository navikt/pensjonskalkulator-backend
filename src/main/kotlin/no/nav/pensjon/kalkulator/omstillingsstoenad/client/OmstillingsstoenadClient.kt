package no.nav.pensjon.kalkulator.omstillingsstoenad.client

import no.nav.pensjon.kalkulator.person.Pid
import java.time.LocalDate

interface OmstillingsstoenadClient {
    fun mottarOmstillingsstoenad(pid: Pid, paaDato: LocalDate): Boolean
}