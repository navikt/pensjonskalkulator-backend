package no.nav.pensjon.kalkulator.tech.representasjon.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon

interface RepresentasjonClient {

    fun hasValidRepresentasjonsforhold(fullmaktGiverPid: Pid): Representasjon
}
