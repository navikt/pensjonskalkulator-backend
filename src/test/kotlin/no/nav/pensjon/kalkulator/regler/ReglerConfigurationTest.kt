package no.nav.pensjon.kalkulator.regler

import no.nav.pensjon.kalkulator.grunnbeloep.client.regler.dto.SatsResponse
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
        val resultater = ReglerConfiguration()
            .objectMapper()
            .readValue(json, SatsResponse::class.java)
            .satsResultater!!

        val resultat1 = resultater[0]
        assertEquals("2021-05-01", resultat1.fom.toString())
        assertEquals("2022-04-30", resultat1.tom.toString())
        assertEquals(106399.0, resultat1.verdi)
        val resultat2 = resultater[1]
        assertEquals("2022-05-01", resultat2.fom.toString())
        assertEquals("9999-12-31", resultat2.tom.toString())
        assertEquals(111477.0, resultat2.verdi)
    }
}
