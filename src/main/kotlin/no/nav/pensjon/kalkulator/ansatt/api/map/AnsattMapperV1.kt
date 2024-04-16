package no.nav.pensjon.kalkulator.ansatt.api.map

import no.nav.pensjon.kalkulator.ansatt.api.dto.AnsattV1

object AnsattMapperV1 {

    fun dtoV1(id: String) = AnsattV1(id)
}
