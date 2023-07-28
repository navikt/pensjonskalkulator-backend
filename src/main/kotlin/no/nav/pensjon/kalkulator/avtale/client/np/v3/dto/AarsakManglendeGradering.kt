package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import no.nav.pensjon.kalkulator.avtale.ManglendeEksternGraderingAarsak

enum class AarsakManglendeGradering(val internalValue: ManglendeEksternGraderingAarsak) {

    // Hvis rettigheten normalt skal kunne graderes, men innretningen ikke kunne levere gradert data.
    // Eller hvis fleksibel startdato ikke støttes.
    IKKE_STOTTET(ManglendeEksternGraderingAarsak.IKKE_STOETTET),

    // Hvis regelverket ikke tillater gradering av denne type rettighet.
    IKKE_TILLATT(ManglendeEksternGraderingAarsak.IKKE_TILLATT)
}
