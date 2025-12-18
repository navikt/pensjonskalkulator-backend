package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.OffentligTjenestepensjonSimuleringFoer1963Resultat
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.SimuleringOffentligTjenestepensjonFoer1963Spec

interface TjenestepensjonSimuleringFoer1963Client {
    fun hentTjenestepensjonSimulering(
        request: SimuleringOffentligTjenestepensjonFoer1963Spec,
        pid: Pid
    ): OffentligTjenestepensjonSimuleringFoer1963Resultat

}
