package no.nav.pensjon.kalkulator.avtale

import org.springframework.util.StringUtils.hasLength

/**
 * Beskrivelse av felter:
 * navn: Påkrevd; navn på selskap som leverer produktet
 * heltUtilgjengelig: Påkrevd; true hvis innretningen ikke ikke kunne nås; false hvis innretningen kunne nås men svarte med feil antall rettigheter
 * antallManglendeRettigheter: Utelates hvis innretningen var helt utilgjengelig, eller hvis Norsk Pensjon av annen grunn ikke kjenner antall manglende rettigheter
 * kategori: Gjenspeiler Kategori i vanlig respons, men brukes bare for «Folketrygd» og «Privat AFP» hvis disse ikke er tilgjengelige
 * feilkode: Videresending av feilkode fra tjeneste som leverer folketrygd og privat AFP
 */
data class Selskap(
    val navn: String,
    val heltUtilgjengelig: Boolean,
    val antallManglendeRettigheter: Int,
    val kategori: AvtaleKategori,
    val feilkode: String
) {
    var harFeil = hasLength(feilkode)

    constructor(navn: String, heltUtilgjengelig: Boolean) :
            this(navn, heltUtilgjengelig, 0, AvtaleKategori.NONE, "")
}
