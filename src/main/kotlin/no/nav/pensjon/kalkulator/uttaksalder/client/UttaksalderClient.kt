package no.nav.pensjon.kalkulator.uttaksalder.client

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.PersonalUttaksalderSpec

interface UttaksalderClient {

    fun finnTidligsteUttaksalder(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec
    ): Alder?
}
