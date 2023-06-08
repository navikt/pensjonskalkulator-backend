package no.nav.pensjon.kalkulator.avtale.client.np.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class PrivatPensjonsrettigheterDto {
    @JacksonXmlProperty(localName = "privatAlderRettigheter")
    var privatAlderRettigheterDto: PrivatAlderRettigheterDto? = null
}
