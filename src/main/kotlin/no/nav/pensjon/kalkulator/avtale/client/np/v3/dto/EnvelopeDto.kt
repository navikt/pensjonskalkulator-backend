package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

class EnvelopeDto {
    @JacksonXmlProperty(namespace = "http://schemas.xmlsoap.org/soap/envelope/", localName = "Body")
    var body: BodyDto? = null
}
