package no.nav.pensjon.kalkulator.tech.security.egress.token.saml

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.kalkulator.mock.Saml
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import java.time.LocalDateTime

class SamlTokenServiceTest : ShouldSpec({

    should("fetch token") {
        val tokenClient = arrangeToken()

        SamlTokenService(
            tokenClient,
            expirationChecker = arrangeTime()
        ).assertion() shouldBe Saml.ASSERTION

        verify(exactly = 1) { tokenClient.fetchSamlToken() }
    }

    should("cache token") {
        val expirationChecker = arrangeTime()
        val tokenClient = arrangeToken()
        val service = SamlTokenService(tokenClient, expirationChecker)

        service.assertion()
        arrangeTokenExpiry(expirationChecker, expired = false)
        service.assertion() // 2nd call shall return the cached token instead of fetching it
        verify(exactly = 1) { tokenClient.fetchSamlToken() }
    }

    should("fetch token again when expired") {
        val expirationChecker = arrangeTime()
        val tokenClient = arrangeToken()
        val service = SamlTokenService(tokenClient, expirationChecker)

        service.assertion()
        arrangeTokenExpiry(expirationChecker, expired = true)
        service.assertion() // 2nd call shall fetch a new token
        verify(exactly = 2) { tokenClient.fetchSamlToken() }
    }
})

private fun arrangeTokenExpiry(checker: ExpirationChecker, expired: Boolean) {
    checker.apply {
        every { isExpired(issuedTime, expiresInSeconds = 1L) } returns expired
    }
}

private fun arrangeTime(): ExpirationChecker =
    mockk<ExpirationChecker>().apply {
        every { time() } returns issuedTime
    }

private fun arrangeToken(): SamlTokenClient =
    mockk<SamlTokenClient>().apply {
        every { fetchSamlToken() } returns samlTokenData
    }

private val issuedTime =
    LocalDateTime.of(2023, 1, 1, 12, 0, 0)

// ENCODED_SAML_ASSERTION = String(Base64.getUrlEncoder().encode(SAML_ASSERTION.toByteArray(StandardCharsets.UTF_8)))
private const val ENCODED_SAML_ASSERTION =
    "PHNhbWwyOkFzc2VydGlvbiB4bWxuczpzYW1sMj0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiIgSUQ9IngiIElz" +
            "c3VlSW5zdGFudD0iMjAyMy0wNi0zMFQxMjoyMjo1MC41MDNaIiBWZXJzaW9uPSIyLjAiPgogICAgICAgICAgICA8c2FtbDI6" +
            "SXNzdWVyPng8L3NhbWwyOklzc3Vlcj4KICAgICAgICAgICAgPFNpZ25hdHVyZSB4bWxucz0iaHR0cDovL3d3dy53My5vcmcv" +
            "MjAwMC8wOS94bWxkc2lnIyI-CiAgICAgICAgICAgICAgICA8U2lnbmVkSW5mbz4KICAgICAgICAgICAgICAgICAgICA8Q2Fu" +
            "b25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIv" +
            "PgogICAgICAgICAgICAgICAgICAgIDxTaWduYXR1cmVNZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAw" +
            "LzA5L3htbGRzaWcjcnNhLXNoYTEiLz4KICAgICAgICAgICAgICAgICAgICA8UmVmZXJlbmNlIFVSST0ieCI-CiAgICAgICAg" +
            "ICAgICAgICAgICAgICAgIDxUcmFuc2Zvcm1zPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPFRyYW5zZm9ybSBBbGdv" +
            "cml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIi8-CiAgICAgICAg" +
            "ICAgICAgICAgICAgICAgICAgICA8VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwt" +
            "ZXhjLWMxNG4jIi8-CiAgICAgICAgICAgICAgICAgICAgICAgIDwvVHJhbnNmb3Jtcz4KICAgICAgICAgICAgICAgICAgICAg" +
            "ICAgPERpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIi8-CiAg" +
            "ICAgICAgICAgICAgICAgICAgICAgIDxEaWdlc3RWYWx1ZT54PC9EaWdlc3RWYWx1ZT4KICAgICAgICAgICAgICAgICAgICA8" +
            "L1JlZmVyZW5jZT4KICAgICAgICAgICAgICAgIDwvU2lnbmVkSW5mbz4KICAgICAgICAgICAgICAgIDxTaWduYXR1cmVWYWx1" +
            "ZT54PC9TaWduYXR1cmVWYWx1ZT4KICAgICAgICAgICAgICAgIDxLZXlJbmZvPgogICAgICAgICAgICAgICAgICAgIDxYNTA5" +
            "RGF0YT4KICAgICAgICAgICAgICAgICAgICAgICAgPFg1MDlDZXJ0aWZpY2F0ZT54PC9YNTA5Q2VydGlmaWNhdGU-CiAgICAg" +
            "ICAgICAgICAgICAgICAgICAgIDxYNTA5SXNzdWVyU2VyaWFsPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPFg1MDlJ" +
            "c3N1ZXJOYW1lPkNOPXgsIERDPXg8L1g1MDlJc3N1ZXJOYW1lPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPFg1MDlT" +
            "ZXJpYWxOdW1iZXI-MTwvWDUwOVNlcmlhbE51bWJlcj4KICAgICAgICAgICAgICAgICAgICAgICAgPC9YNTA5SXNzdWVyU2Vy" +
            "aWFsPgogICAgICAgICAgICAgICAgICAgIDwvWDUwOURhdGE-CiAgICAgICAgICAgICAgICA8L0tleUluZm8-CiAgICAgICAg" +
            "ICAgIDwvU2lnbmF0dXJlPgogICAgICAgICAgICA8c2FtbDI6U3ViamVjdD4KICAgICAgICAgICAgICAgIDxzYW1sMjpOYW1l" +
            "SUQgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoxLjE6bmFtZWlkLWZvcm1hdDp1bnNwZWNpZmllZCI-eDwvc2Ft" +
            "bDI6TmFtZUlEPgogICAgICAgICAgICAgICAgPHNhbWwyOlN1YmplY3RDb25maXJtYXRpb24gTWV0aG9kPSJ1cm46b2FzaXM6" +
            "bmFtZXM6dGM6U0FNTDoyLjA6Y206YmVhcmVyIj4KICAgICAgICAgICAgICAgICAgICA8c2FtbDI6U3ViamVjdENvbmZpcm1h" +
            "dGlvbkRhdGEgTm90QmVmb3JlPSIyMDIzLTA2LTMwVDEyOjIyOjUwLjUwM1oiIE5vdE9uT3JBZnRlcj0iMjAyMy0wNi0zMFQx" +
            "MzoyMjozNy41MDNaIi8-CiAgICAgICAgICAgICAgICA8L3NhbWwyOlN1YmplY3RDb25maXJtYXRpb24-CiAgICAgICAgICAg" +
            "IDwvc2FtbDI6U3ViamVjdD4KICAgICAgICAgICAgPHNhbWwyOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDIzLTA2LTMwVDEy" +
            "OjIyOjUwLjUwM1oiIE5vdE9uT3JBZnRlcj0iMjAyMy0wNi0zMFQxMzoyMjozNy41MDNaIi8-CiAgICAgICAgICAgIDxzYW1s" +
            "MjpBdHRyaWJ1dGVTdGF0ZW1lbnQ-CiAgICAgICAgICAgICAgICA8c2FtbDI6QXR0cmlidXRlIE5hbWU9ImNvbnN1bWVySWQi" +
            "IE5hbWVGb3JtYXQ9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphdHRybmFtZS1mb3JtYXQ6dXJpIj4KICAgICAgICAg" +
            "ICAgICAgICAgICA8c2FtbDI6QXR0cmlidXRlVmFsdWU-eDwvc2FtbDI6QXR0cmlidXRlVmFsdWU-CiAgICAgICAgICAgICAg" +
            "ICA8L3NhbWwyOkF0dHJpYnV0ZT4KICAgICAgICAgICAgICAgIDxzYW1sMjpBdHRyaWJ1dGUgTmFtZT0iYXVkaXRUcmFja2lu" +
            "Z0lkIiBOYW1lRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YXR0cm5hbWUtZm9ybWF0OnVyaSI-CiAgICAg" +
            "ICAgICAgICAgICAgICAgPHNhbWwyOkF0dHJpYnV0ZVZhbHVlPng8L3NhbWwyOkF0dHJpYnV0ZVZhbHVlPgogICAgICAgICAg" +
            "ICAgICAgPC9zYW1sMjpBdHRyaWJ1dGU-CiAgICAgICAgICAgIDwvc2FtbDI6QXR0cmlidXRlU3RhdGVtZW50PgogICAgICAg" +
            "IDwvc2FtbDI6QXNzZXJ0aW9uPg=="

private val samlTokenData =
    SamlTokenDataDto(
        access_token = ENCODED_SAML_ASSERTION,
        issued_token_type = "",
        token_type = "",
        expires_in = 1
    )
