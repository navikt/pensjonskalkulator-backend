package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.vedtak.client.LoependeVedtakClient
import org.springframework.stereotype.Service

@Service
class LoependeVedtakService(
    private val vedtakClient: LoependeVedtakClient,
    private val pidGetter: PidGetter
) {
    fun hentLoependeVedtak(): VedtakSamling =
        vedtakClient.hentLoependeVedtak(pidGetter.pid())
}
