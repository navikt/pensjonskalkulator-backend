package no.nav.pensjon.kalkulator.tech.representasjon.client

import no.nav.pensjon.kalkulator.person.EncryptedPid
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon

interface RepresentasjonClient {

    fun hasValidRepresentasjonsforhold(fullmaktsgiverPid: EncryptedPid): Representasjon
}