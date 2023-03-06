package no.nav.pensjon.kalkulator.grunnbeloep.client

import no.nav.pensjon.kalkulator.grunnbeloep.Grunnbeloep

interface GrunnbeloepClient {

    fun getGrunnbeloep(spec: GrunnbeloepSpec): Grunnbeloep
}
