package no.nav.pensjon.kalkulator.avtale.client.np.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class FaultDto {
    var faultcode: String? = null
    var faultstring: String? = null
    var faultactor: String? = null
    var detail: FaultDetailDto? = null
}

class FaultDetailDto {
    @JacksonXmlProperty(localName = "transaction-id")
    var transactionId: String? = null

    @JacksonXmlProperty(localName = "global-transaction-id")
    var globalTransactionId: String? = null
}
