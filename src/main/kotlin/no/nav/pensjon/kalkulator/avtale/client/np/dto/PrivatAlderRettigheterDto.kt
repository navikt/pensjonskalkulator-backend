package no.nav.pensjon.kalkulator.avtale.client.np.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class PrivatAlderRettigheterDto {
    var produktbetegnelse: String? = null
    var kategori: String? = null
    var startAlder: Int? = null
    var sluttAlder: Int? = null

    @JacksonXmlElementWrapper(useWrapping = false)
    var utbetalingsperioder: List<UtbetalingsperioderDto>? = null
}
