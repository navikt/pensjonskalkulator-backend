package no.nav.pensjon.kalkulator.simulering.client

import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat

interface SimuleringClient {
    fun simulerAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ): Simuleringsresultat

    fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): Simuleringsresultat
}
