package no.nav.pensjon.kalkulator.tech.security.egress.config

import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget

/**
 * Informasjon som bestemmer hvilken type av token (JWT) som skal brukes for egress-kall (kall mot andre apper).
 */
data class EgressTokenTypeDecisionBasis(
    val representasjonTarget: RepresentasjonTarget,
    val ingressToken: String? = null
)