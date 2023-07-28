package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(namespace = "http://schemas.xmlsoap.org/soap/envelope/", localName = "Envelope")
class EnvelopeDto {
    @JacksonXmlProperty(namespace = "http://schemas.xmlsoap.org/soap/envelope/", localName = "Body")
    var body: BodyDto? = null
}
