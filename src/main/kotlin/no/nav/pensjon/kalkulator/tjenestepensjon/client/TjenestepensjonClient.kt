package no.nav.pensjon.kalkulator.tjenestepensjon.client

import no.nav.pensjon.kalkulator.person.Pid
import java.time.LocalDate

interface TjenestepensjonClient {
    fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean
}
