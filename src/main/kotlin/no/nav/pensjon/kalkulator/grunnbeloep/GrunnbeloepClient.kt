package no.nav.pensjon.kalkulator.grunnbeloep

import no.nav.pensjon.kalkulator.grunnbeloep.regler.dto.SatsResponse

interface GrunnbeloepClient {

    fun getGrunnbeloep(requestBody: String): SatsResponse
}
