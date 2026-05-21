package no.nav.pensjon.kalkulator.lagring.client.sanity

import no.nav.pensjon.kalkulator.lagring.ForbeholdInnhold

interface ForbeholdClient {
    fun fetchForbehold(): ForbeholdInnhold?
}
