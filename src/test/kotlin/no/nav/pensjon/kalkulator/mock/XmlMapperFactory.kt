package no.nav.pensjon.kalkulator.mock

import tools.jackson.databind.DeserializationFeature
import tools.jackson.dataformat.xml.XmlMapper

object XmlMapperFactory {

    fun xmlMapper(): XmlMapper =
        XmlMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()
}
