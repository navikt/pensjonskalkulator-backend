package no.nav.pensjon.kalkulator.avtale.client

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.person.Pid

interface PensjonsavtaleClient {
    fun fetchAvtaler(spec: PensjonsavtaleSpec, pid: Pid): Pensjonsavtaler
}
