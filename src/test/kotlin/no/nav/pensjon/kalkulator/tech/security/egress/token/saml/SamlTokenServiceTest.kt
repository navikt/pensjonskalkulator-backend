package no.nav.pensjon.kalkulator.tech.security.egress.token.saml

import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.SamlTokenClient
import no.nav.pensjon.kalkulator.tech.security.egress.token.saml.client.gandalf.dto.SamlTokenDataDto
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.ExpirationChecker
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)

class SamlTokenServiceTest {

    private lateinit var tokenService: SamlTokenService

    @Mock
    private lateinit var tokenClient: SamlTokenClient

    @Mock
    private lateinit var expirationChecker: ExpirationChecker

    @BeforeEach
    fun initialize() {
        `when`(tokenClient.fetchSamlToken()).thenReturn(samlTokenData)
        `when`(expirationChecker.time()).thenReturn(issuedTime)
        tokenService = SamlTokenService(tokenClient, expirationChecker)
    }

    @Test
    fun `assertion fetches token`() {
        assertEquals(SAML_ASSERTION, tokenService.assertion())
        verify(tokenClient, times(1)).fetchSamlToken()
    }

    @Test
    fun `assertion caches token`() {
        tokenService.assertion()
        `when`(expirationChecker.isExpired(issuedTime, 1L)).thenReturn(false)
        tokenService.assertion() // 2nd call shall return cached token instead of fetching it

        verify(tokenClient, times(1)).fetchSamlToken()
    }

    @Test
    fun `assertion fetches token again when expired`() {
        tokenService.assertion()
        `when`(expirationChecker.isExpired(issuedTime, 1L)).thenReturn(true)
        tokenService.assertion() // 2nd call shall fetch new token

        verify(tokenClient, times(2)).fetchSamlToken()
    }

    companion object {
        private val issuedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0)

        @Language("xml")
        const val SAML_ASSERTION =
            """<saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion" ID="x" IssueInstant="2023-06-30T12:22:50.503Z" Version="2.0">
            <saml2:Issuer>x</saml2:Issuer>
            <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
                <SignedInfo>
                    <CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
                    <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
                    <Reference URI="x">
                        <Transforms>
                            <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
                            <Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
                        </Transforms>
                        <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                        <DigestValue>x</DigestValue>
                    </Reference>
                </SignedInfo>
                <SignatureValue>x</SignatureValue>
                <KeyInfo>
                    <X509Data>
                        <X509Certificate>x</X509Certificate>
                        <X509IssuerSerial>
                            <X509IssuerName>CN=x, DC=x</X509IssuerName>
                            <X509SerialNumber>1</X509SerialNumber>
                        </X509IssuerSerial>
                    </X509Data>
                </KeyInfo>
            </Signature>
            <saml2:Subject>
                <saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">x</saml2:NameID>
                <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
                    <saml2:SubjectConfirmationData NotBefore="2023-06-30T12:22:50.503Z" NotOnOrAfter="2023-06-30T13:22:37.503Z"/>
                </saml2:SubjectConfirmation>
            </saml2:Subject>
            <saml2:Conditions NotBefore="2023-06-30T12:22:50.503Z" NotOnOrAfter="2023-06-30T13:22:37.503Z"/>
            <saml2:AttributeStatement>
                <saml2:Attribute Name="consumerId" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                    <saml2:AttributeValue>x</saml2:AttributeValue>
                </saml2:Attribute>
                <saml2:Attribute Name="auditTrackingId" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                    <saml2:AttributeValue>x</saml2:AttributeValue>
                </saml2:Attribute>
            </saml2:AttributeStatement>
        </saml2:Assertion>"""


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
    }

    private val samlTokenData = SamlTokenDataDto(ENCODED_SAML_ASSERTION, "", "", 1)
}
