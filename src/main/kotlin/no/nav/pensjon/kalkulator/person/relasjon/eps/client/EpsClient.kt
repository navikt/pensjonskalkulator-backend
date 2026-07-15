package no.nav.pensjon.kalkulator.person.relasjon.eps.client

import no.nav.pensjon.kalkulator.person.relasjon.Familierelasjon

/**
 * EPS = ektefelle/partner/samboer
 */
interface EpsClient {

    fun fetchNaavaerendeEps(spec: NaavaerendeEpsSpec): Familierelasjon

    fun fetchNyligsteEps(spec: NyligsteEpsSpec): Familierelasjon

    fun fetchTidligereGiftEllerBarnMed(spec: TidligereStatusSpec): Boolean
}