package no.nav.pensjon.kalkulator.tp.client.esb.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(namespace = "http://schemas.xmlsoap.org/soap/envelope/", localName = "Envelope")
class EnvelopeDto {
    @JacksonXmlProperty(namespace = "http://schemas.xmlsoap.org/soap/envelope/", localName = "Body")
    var body: BodyDto? = null
}

class BodyDto {
    @JacksonXmlProperty(
        namespace = "http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf",
        localName = "finnTjenestepensjonForholdResponse"
    )
    var wrapper: WrapperDto? = null
}

class WrapperDto {
    @JacksonXmlProperty(localName = "finnTjenestepensjonForholdResponse")
    var response: ResponseDto? = null
}

class ResponseDto {
    @JacksonXmlProperty(localName = "fnr")
    var pid: String? = null

    @JacksonXmlProperty(localName = "tjenestepensjonForholdene")
    var forhold: ForholdDto? = null
}

class ForholdDto {
    var forholdId: Int? = null
    var tssEksternId: Long? = null
    var navn: String? = null
    var tpNr: Int? = null
    var harUtlandPensjon: Boolean? = null
    var samtykkeSimuleringKode: String? = null
    var harSimulering: Boolean? = null
    var tjenestepensjonYtelseListe: YtelseDto? = null
    var endringsInfo: EndringsinfoDto? = null
}

class YtelseDto {
    var ytelseId: Int? = null
    var innmeldtFom: String? = null //TODO date
    var ytelseKode: String? = null
    var ytelseBeskrivelse: String? = null
    var iverksattFom: String? = null //TODO date
}

class EndringsinfoDto {
    var endretAvId: String? = null
    var opprettetAvId: String? = null
    var endretDato: String? = null //TODO date
    var opprettetDato: String? = null //TODO date
}
