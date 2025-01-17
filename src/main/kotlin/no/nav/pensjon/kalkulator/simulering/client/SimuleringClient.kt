package no.nav.pensjon.kalkulator.simulering.client

import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringResult

interface SimuleringClient {
    fun simulerAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ): SimuleringResult
}

interface AnonymSimuleringClient {
    fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult
}
