package no.nav.pensjon.kalkulator.avtale.client.np.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class PrivatPensjonsrettigheterDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    var privatAlderRettigheter: List<PrivatAlderRettigheterDto>? = null

    @JacksonXmlElementWrapper(useWrapping = false)
    var utilgjengeligeSelskap: List<SelskapDto>? = null
}
