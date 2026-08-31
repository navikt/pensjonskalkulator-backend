package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.vedtak.client.InnledningClient
import org.springframework.stereotype.Service

@Service
class InnledningService(
    private val innledningClient: InnledningClient,
    private val pidGetter: PidGetter
) {
    fun hentInnledningsdata(): Innledningsdata =
        innledningClient.hentInnledningsdata(pidGetter.pid())
}
