package no.nav.pensjon.kalkulator.lagring.client.sanity

import no.nav.pensjon.kalkulator.lagring.ForbeholdOgKortforbehold

interface ForbeholdClient {
    fun fetchForbeholdOgKortforbehold(): ForbeholdOgKortforbehold
}
