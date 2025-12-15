package no.nav.pensjon.kalkulator.mock

import org.intellij.lang.annotations.Language

object Saml {
    @Language("xml")
    const val ASSERTION =
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
}