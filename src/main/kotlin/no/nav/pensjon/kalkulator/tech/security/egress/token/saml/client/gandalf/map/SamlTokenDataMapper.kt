package no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.map

import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.SamlTokenData
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*

object SamlTokenDataMapper {
    fun map(dto: SamlTokenDataDto, time: LocalDateTime) =
        SamlTokenData(
            decode(dto.access_token),
            time,
            dto.expires_in.toLong()
        )

    private fun decode(token: String) = String(Base64.getUrlDecoder().decode(bytes(token)))

    private fun bytes(token: String) = token.toByteArray(StandardCharsets.UTF_8)
}
