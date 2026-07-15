package no.nav.pensjon.kalkulator.tech.security.egress.maskinporten

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.jwtbearer.JwtBearerAssertionBasis
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MaskinportenJwtBearerAssertionBasis(
    @param:Value($$"${maskinporten.client.id}") override val clientId: String,
    @param:Value($$"${maskinporten.client.jwk}") override val clientJwk: String,
    @param:Value($$"${maskinporten.issuer}") override val issuer: String,
) : JwtBearerAssertionBasis