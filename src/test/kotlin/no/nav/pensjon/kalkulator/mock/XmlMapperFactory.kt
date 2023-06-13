package no.nav.pensjon.kalkulator.mock

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper

object XmlMapperFactory {

    fun xmlMapper() =
        XmlMapper().apply {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
}
