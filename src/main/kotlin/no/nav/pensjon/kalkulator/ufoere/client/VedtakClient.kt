package no.nav.pensjon.kalkulator.ufoere.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.ufoere.Vedtak
import java.time.LocalDate

interface VedtakClient {
    fun bestemGjeldendeVedtak(pid: Pid, fom: LocalDate): List<Vedtak>
}
