package no.nav.pensjon.kalkulator.avtale.client

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec

interface PensjonsavtaleClient {
    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtaler
}
