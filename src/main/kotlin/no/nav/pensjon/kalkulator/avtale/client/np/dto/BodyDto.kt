package no.nav.pensjon.kalkulator.avtale.client.np.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
/*
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="id-f7685e25-59eb-4a98-8cd3-fba5d80ea421" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:privatPensjonsrettigheter xmlns:ns2="http://norskpensjon.no/api/pensjon/V2_0/typer">
            <privatAlderRettigheter>
                <produktbetegnelse>PENSJONSKAPITALBEVIjS</produktbetegnelse>
                <kategori>innskuddsbasertKollektiv</kategori>
                <startAlder>76</startAlder>
                <sluttAlder>86</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>76</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>86</sluttAlder>
                    <sluttMaaned>1</sluttMaaned>
                    <aarligUtbetaling>34000</aarligUtbetaling>
                    <grad>100</grad>
                </utbetalingsperioder>
            </privatAlderRettigheter>
        </ns2:privatPensjonsrettigheter>
    </soap:Body>
</soap:Envelope>
*/
class BodyDto {
    @JacksonXmlProperty(
        namespace = "http://norskpensjon.no/api/pensjon/V2_0/typer",
        localName = "privatPensjonsrettigheter"
    )
    var privatPensjonsrettigheter: PrivatPensjonsrettigheterDto? = null
}


