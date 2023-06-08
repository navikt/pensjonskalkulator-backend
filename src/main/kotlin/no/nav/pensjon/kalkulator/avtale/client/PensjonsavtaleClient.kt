package no.nav.pensjon.kalkulator.avtale.client

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec

interface PensjonsavtaleClient {
    fun fetchAvtaler(spec: PensjonsavtaleSpec): Pensjonsavtale
}
