package no.nav.pensjon.kalkulator.avtale

/**
 * Beskrivelse av felter:
 * selskapsnavn: Påkrevd; navn på selskap som leverer produktet
 * produktbetegnelse: Påkrevd; produktbetegnelse
 * arbeidsgiver: Navn på arbeidsgiver
 * kategori: Påkrevd; kategori
 * naavaerendeAvtaltAarligInnskudd: Nåværende avtalt årlig innskudd
 * beregningsmodell: Leverandør av prognosen
 * startAar: Første startår (alder)
 * sluttAar: Siste sluttår (alder)
 * opplysningsdato: Dato (YYYY-MM-DD)
 * aarsakManglendeGradering: Årsak til manglende gradering; attributtet er satt hvis pensjonen ikke kan bestå av graderte data, eller ikke kan simuleres fleksibelt (dvs. startalder ulik ordinær startalder for pensjon 67 år)
 * utbetalingsperioder: Hvis en rettighet ikke kan leveres med utbetalingsperioder, så skal «Årsak til manglende utbetaling» ha en relevant feilkode.
 */
data class Pensjonsavtale(
    val avtalenummer: String,
    val arbeidsgiver: String,
    val selskapsnavn: String,
    val produktbetegnelse: String,
    val kategori: AvtaleKategori,
    val underkategori: AvtaleUnderkategori,
    val innskuddssaldo: Int,
    val naavaerendeAvtaltAarligInnskudd: Int,
    val pensjonsbeholdningForventet: Int,
    val pensjonsbeholdningNedreGrense: Int,
    val pensjonsbeholdningOvreGrense: Int,
    val avkastningsgaranti: Boolean,
    val beregningsmodell: EksternBeregningsmodell,
    val startAar: Int, // år som i alder (antall fylte år etter fødselsdato)
    val sluttAar: Int?, // år som i alder
    val opplysningsdato: String,
    val manglendeGraderingAarsak: ManglendeEksternGraderingAarsak,
    val manglendeBeregningAarsak: ManglendeEksternBeregningAarsak,
    val utbetalingsperioder: List<Utbetalingsperiode>
) {
    val harStartAar = startAar > 0
    val erLivsvarig = sluttAar == null

    constructor(
        produktbetegnelse: String,
        kategori: AvtaleKategori,
        startalder: Int,
        sluttalder: Int?,
        utbetalingsperioder: List<Utbetalingsperiode>
    ) : this(
        "",
        "",
        "",
        produktbetegnelse,
        kategori,
        AvtaleUnderkategori.NONE,
        0,
        0,
        0,
        0,
        0,
        false,
        EksternBeregningsmodell.NONE,
        startalder,
        sluttalder,
                "1901-01-01",
        ManglendeEksternGraderingAarsak.NONE,
        ManglendeEksternBeregningAarsak.NONE,
        utbetalingsperioder
    )
}
