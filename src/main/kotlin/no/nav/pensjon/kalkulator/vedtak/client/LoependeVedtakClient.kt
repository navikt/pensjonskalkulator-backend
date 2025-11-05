package no.nav.pensjon.kalkulator.vedtak.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.vedtak.VedtakSamling

interface LoependeVedtakClient {
    fun hentLoependeVedtak(pid: Pid): VedtakSamling
}