package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

private fun jsonStr(value: String?): String = if (value == null) "null" else "\"$value\""

class NorskPensjonResult {
    var pensjonsRettigheter: List<NorskPensjonPensjonsrettighet>? = null
    var utilgjengeligeInnretninger: List<NorskPensjonUtilgjengeligInnretning>? = null
    override fun toString(): String {
        return "{\"pensjonsRettigheter\":$pensjonsRettigheter,\"utilgjengeligeInnretninger\":$utilgjengeligeInnretninger}"
    }

}

class NorskPensjonUtilgjengeligInnretning {
    var selskapsnavn: String? = null
    var heltUtilgjengelig: Boolean? = null
    var antallManglendeRettigheter: Int? = null
    var kategori: String? = null
    var feilkode: String? = null
    override fun toString(): String {
        return "{\"selskapsnavn\":${jsonStr(selskapsnavn)},\"heltUtilgjengelig\":$heltUtilgjengelig,\"antallManglendeRettigheter\":$antallManglendeRettigheter,\"kategori\":${jsonStr(kategori)},\"feilkode\":${jsonStr(feilkode)}}"
    }

}

class NorskPensjonPensjonsrettighet {
    var avtalenummer: String? = null
    var arbeidsgiver: String? = null
    var selskapsnavn: String? = null
    var produktbetegnelse: String? = null
    var kategori: String? = null
    var underkategori: String? = null
    var merknad: String? = null
    var innskuddssaldo: Int? = null
    var naavaerendeAvtaltAarligInnskudd: Int? = null
    var pensjonsbeholdningForventet: Int? = null
    var avkastningsgaranti: Boolean? = null
    var beregningsmodell: String? = null
    var startAlder: Int? = null
    var sluttAlder: Int? = null
    var utbetalingsperioder: List<UtbetalingsperiodeDto>? = null
    var opplysningsdato: String? = null
    var aarsakManglendeGradering: String? = null
    var aarsakIkkeBeregnet: String? = null
    override fun toString(): String {
        return "{\"avtalenummer\":${jsonStr(avtalenummer)},\"arbeidsgiver\":${jsonStr(arbeidsgiver)},\"selskapsnavn\":${jsonStr(selskapsnavn)},\"produktbetegnelse\":${jsonStr(produktbetegnelse)},\"kategori\":${jsonStr(kategori)},\"underkategori\":${jsonStr(underkategori)},\"merknad\":${jsonStr(merknad)},\"innskuddssaldo\":$innskuddssaldo,\"naavaerendeAvtaltAarligInnskudd\":$naavaerendeAvtaltAarligInnskudd,\"pensjonsbeholdningForventet\":$pensjonsbeholdningForventet,\"avkastningsgaranti\":$avkastningsgaranti,\"beregningsmodell\":${jsonStr(beregningsmodell)},\"startAlder\":$startAlder,\"sluttAlder\":$sluttAlder,\"utbetalingsperioder\":$utbetalingsperioder,\"opplysningsdato\":${jsonStr(opplysningsdato)},\"aarsakManglendeGradering\":${jsonStr(aarsakManglendeGradering)},\"aarsakIkkeBeregnet\":${jsonStr(aarsakIkkeBeregnet)}}"
    }

}

enum class NorskPensjonError(val beskrivelse: String) {
    UGYLDIG_FOEDSELSNUMMER(beskrivelse = "Ugyldig fødselsnummer"),
    UGYLDIG_MAKS_ALDER_SIMULERING(beskrivelse = "Personen er eldre enn maks simuleringsalder (75 år)"),
    INGEN_UTTAKSPERIODER_FUNNET(beskrivelse = "Ingen uttaksperioder oppgitt i request"),
    INGEN_UTTAKSPERIODER_FULL_UTTAK_FUNNET(beskrivelse = "Ingen uttaksperiode har grad = 100"),
    FLERE_UTTAKSPERIODER_FULL_UTTAK_FUNNET(beskrivelse = "Mer enn én uttaksperiode har grad = 100"),
    NEGATIVE_TALL_IKKE_TILLATT(beskrivelse = "Et numerisk felt inneholder en negativ verdi"),
    UGYLDIG_VERDI_BELOEP(beskrivelse = "Beløpsfelt overskrider maksverdi (2 000 000 000)"),
    UGYLDIG_VERDI_ANTALL_AAR_INNTEKT_ETTER_UTTAK(beskrivelse = "Antall inntektsår etter uttak overskrider maks tillatt for personen"),
    UGYLDIG_VERDI_STARTMAANED(beskrivelse = "Startmåned er utenfor gyldig intervall"),
    UGYLDIG_VERDI_STARTALDER_OG_STARTMAANED(beskrivelse = "Kombinasjonen av startAlder og startMaaned er ugyldig for personen"),
    UGYLDIG_VERDI_GRAD(beskrivelse = "Grad er ikke i intervallet [20..100]"),
}