package no.nav.pensjon.kalkulator.uttaksalder.client

import no.nav.pensjon.kalkulator.uttaksalder.Alder
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec

interface UttaksalderClient {

    fun finnTidligsteUttaksalder(spec: UttaksalderSpec): Alder?
}
