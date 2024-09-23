package no.nav.pensjon.kalkulator.vedtak.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtak

interface LoependeVedtakClient {
    fun hentLoependeVedtak(pid: Pid): LoependeVedtak
}