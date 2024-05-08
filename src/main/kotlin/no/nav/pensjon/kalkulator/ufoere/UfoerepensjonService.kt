package no.nav.pensjon.kalkulator.ufoere

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.ufoere.client.UfoeregradClient
import no.nav.pensjon.kalkulator.ufoere.client.VedtakClient
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UfoerepensjonService(
    private val vedtakClient: VedtakClient,
    private val ufoeregradClient: UfoeregradClient,
    private val pidGetter: PidGetter
) {
     fun harLoependeUfoerepensjon(fom: LocalDate) =
        vedtakClient.bestemGjeldendeVedtak(pidGetter.pid(), fom)
            .any { Sakstype.UFOEREPENSJON == it.sakstype }

    fun hentUfoeregrad() = ufoeregradClient.hentUfoeregrad(pidGetter.pid())
}
