package no.nav.pensjon.kalkulator.avtale.client.np.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class BodyDto {
    @JacksonXmlProperty(
        namespace = "http://norskpensjon.no/api/pensjon/V2_0/typer",
        localName = "privatPensjonsrettigheter"
    )
    var privatPensjonsrettigheter: PrivatPensjonsrettigheterDto? = null

    @JacksonXmlProperty(
        namespace = "http://schemas.xmlsoap.org/soap/envelope/",
        localName = "Fault"
    )
    var fault: FaultDto? = null
}


