package no.nav.pensjon.kalkulator.tech.representasjon.client

import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonSpec

interface RepresentasjonClient {

    fun fetchRepresentasjon(spec: RepresentasjonSpec): Representasjon
}