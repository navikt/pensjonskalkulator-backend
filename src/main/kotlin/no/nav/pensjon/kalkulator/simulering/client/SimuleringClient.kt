package no.nav.pensjon.kalkulator.simulering.client

import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringResult

interface SimuleringClient {
    /**
     * Simulering der personens identitet er ukjent.
     * Alle personopplysninger må oppgis av bruker.
     */
    fun simulerAnonymAlderspensjon(spec: ImpersonalSimuleringSpec): SimuleringResult

    /**
     * Simulering der personens identitet er kjent (typisk ved fødselsnummer).
     * Personopplysninger kan da hentes fra Nav-systemer.
     */
    fun simulerPersonligAlderspensjon(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ): SimuleringResult
}
