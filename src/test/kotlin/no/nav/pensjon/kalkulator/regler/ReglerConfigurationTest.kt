package no.nav.pensjon.kalkulator.regler

import no.nav.pensjon.kalkulator.grunnbeloep.regler.dto.SatsResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ReglerConfigurationTest {

    @Test
    fun objectMapper_maps_json_to_object() {
        val json = """{
    "satsResultater": [
        "java.util.ArrayList",
        [
            {
                "fom": 1619863200000,
                "tom": 1651312800000,
                "verdi": 106399.0
            },
            {
                "fom": 1651399200000,
                "tom": 253402254000000,
                "verdi": 111477.0
            }
        ]
    ]
}"""
        val value = ReglerConfiguration().objectMapper().readValue(json, SatsResponse::class.java)

        assertEquals("SatsResponse(satsResultater=[" +
                "SatsResultat(fom=Sat May 01 12:00:00 CEST 2021, tom=Sat Apr 30 12:00:00 CEST 2022, verdi=106399.0), " +
                "SatsResultat(fom=Sun May 01 12:00:00 CEST 2022, tom=Fri Dec 31 12:00:00 CET 9999, verdi=111477.0)])",
            value.toString())
    }
}
