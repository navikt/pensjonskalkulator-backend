package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    private val avtaleClient: PensjonsavtaleClient,
    private val pidGetter: PidGetter
) {
    fun fetchAvtaler(): Pensjonsavtaler {
        return avtaleClient.fetchAvtaler(pidGetter.pid())
    }
}
