package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class PensjonsrettighetDto {
    var avtalenummer: String? = null
    var arbeidsgiver: String? = null
    var selskapsnavn: String? = null
    var produktbetegnelse: String? = null
    var kategori: String? = null
    var underkategori: String? = null
    var innskuddssaldo: Int? = null
    var naavaerendeAvtaltAarligInnskudd: Int? = null
    var pensjonsbeholdningForventet: Int? = null
    var pensjonsbeholdningNedreGrense: Int? = null
    var pensjonsbeholdningOvreGrense: Int? = null
    var avkastningsgaranti: Boolean? = null
    var beregningsmodell: String? = null
    var startAlder: Int? = null
    var sluttAlder: Int? = null
    var aarsakManglendeGradering: String? = null
    var aarsakIkkeBeregnet: String? = null
    var opplysningsdato: String? = null

    @JacksonXmlElementWrapper(useWrapping = false)
    var utbetalingsperioder: List<UtbetalingsperiodeDto>? = null
}
