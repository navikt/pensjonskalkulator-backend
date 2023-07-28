package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import no.nav.pensjon.kalkulator.avtale.ManglendeEksternBeregningAarsak

enum class AarsakIkkeBeregnet(val internalValue: ManglendeEksternBeregningAarsak) {

    // Rettigheten har en ugyldig kombinasjon av hovedkategori og underkategori, og kunne ikke beregnes pga. dette.
    UKJENT_PRODUKTTYPE(ManglendeEksternBeregningAarsak.UKJENT_PRODUKTTYPE)
}
