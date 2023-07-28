package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class BodyDto {
    @JacksonXmlProperty(
        namespace = "http://norskpensjon.no/api/pensjonskalkulator/v3/typer",
        localName = "pensjonsrettigheter"
    )
    var pensjonsrettigheter: PensjonsrettigheterDto? = null

    @JacksonXmlProperty(
        namespace = "http://schemas.xmlsoap.org/soap/envelope/",
        localName = "Fault"
    )
    var fault: FaultDto? = null
}
