package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.skjerming.client.SkjermingClient
import org.springframework.stereotype.Service

@Service
class SkjermingService(private val client: SkjermingClient) {

    fun personErTilgjengelig(pid: Pid): Boolean = client.personErTilgjengelig(pid)
}
