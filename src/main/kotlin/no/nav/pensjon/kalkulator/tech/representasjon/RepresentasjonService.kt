package no.nav.pensjon.kalkulator.tech.representasjon

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.representasjon.client.RepresentasjonClient
import org.springframework.stereotype.Component

@Component
class RepresentasjonService(private val client: RepresentasjonClient) {

    fun hasValidRepresentasjonsforhold(fullmaktGiverPid: Pid): Representasjon =
        client.hasValidRepresentasjonsforhold(fullmaktGiverPid)
}
