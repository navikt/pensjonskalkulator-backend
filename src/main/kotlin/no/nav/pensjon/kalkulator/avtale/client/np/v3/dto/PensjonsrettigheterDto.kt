package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class PensjonsrettigheterDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    var pensjonsRettigheter: List<PensjonsrettighetDto>? = null

    @JacksonXmlElementWrapper(useWrapping = false)
    var utilgjengeligeInnretninger: List<UtilgjengeligInnretningDto>? = null
}
