package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

import no.nav.pensjon.kalkulator.avtale.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid

/**
 * DTO som spesifiserer parametre for henting av avtaler fra Norsk Pensjon.
 *
 * Beskrivelse av felter:
 * pid: Påkrevd; person-identifikator, dvs. fødselsnummer eller D-nummer
 * aarligInntektFoerUttak: Påkrevd; årlig inntekt før uttak
 * uttaksperioder: Påkrevd; én uttaksperiode oppgis hvis det bare er 100% uttak, mens to uttaksperioder må oppgis hvis det er gradert uttak, hvorav siste må være 100% uttak
 * antallInntektsaarEtterUttak: Påkrevd; antall inntektsår under helt uttak (0–14); verdiene 0–13 er å anse som reelle antall år, mens 14 betyr livsvarig
 * harAfp: Påkrevd; har privat AFP?
 * harEpsPensjon: Hvorvidt ektefelle/partner/samboer kommer til å motta pensjon (settes til «true» om den ikke sendes med)
 * harEpsPensjonsgivendeInntektOver2G: Hvorvidt ektefelle/partner/samboer kommer til å ha inntektsgivende lønn over 2 ganger grunnbeløpet (settes til «true» om den ikke sendes med)
 * oenskesSimuleringAvFolketrygd: Påkrevd; ønskes simulering av folketrygd?
 * antallAarIUtlandetEtter16: Antall år i utlandet etter fylte 16 år (settes til 0 om det ikke sendes med)
 * sivilstatus: Settes til «gift» om det ikke sendes med
 */
data class NorskPensjonPensjonsavtaleSpecDto(
    val pid: Pid,
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<NorskPensjonUttaksperiodeSpecDto>,
    val antallInntektsaarEtterUttak: Int,
    val harAfp: Boolean = false,
    val harEpsPensjon: Boolean = true, // Norsk Pensjon default
    val harEpsPensjonsgivendeInntektOver2G: Boolean = true, // Norsk Pensjon default
    val antallAarIUtlandetEtter16: Int = 0,
    val sivilstatus: Sivilstatus = Sivilstatus.GIFT, // Norsk Pensjon default
    val oenskesSimuleringAvFolketrygd: Boolean = false
)

/**
 * Beskrivelse av felter:
 * start: Må resultere i en dato tidligst inneværende måned kallet blir gjort.
 *        Månedsverdien i startalder er relativ til, og etter, brukerens fødselsmåned.
 *        F.eks., for en bruker født i april (4), så skal startmåned 2 tolkes som førstkommende juni (6 = 4 + 2) etter at brukeren har fylt startalder.
 *        Dette kan bety en reell dato i året etter at brukeren har fylt startalder.
 * grad: Uttaksgrad; i den siste uttaksperioden må grad være 100.
 * aarligInntekt: Årlig inntekt i uttaksperioden.
 */
data class NorskPensjonUttaksperiodeSpecDto(
    val start: NorskPensjonAlderDto,
    val grad: Uttaksgrad,
    val aarligInntekt: Int
)

/**
 * Alder i år og måned (angitt i henhold til Norsk Pensjons spesifikasjon).
 * Månedsverdi er 1 til 12 og betegner avstanden mellom måneden i fødselsdatoen og måneden i datoen da alderen skal angis.
 * Eksempel: Fødselsdato 15.4.1963. Alder skal angis 1.5.2033. Resultatet er en alder av 70 år og 1 måned
 *           (siden 2033–1963 = 70, og 5–4 = 1)
 *
 * Beskrivelse av felter:
 * aar: Påkrevd; antall fylte hele år
 * maaned: Påkrevd; måned (1..12). Verdien er relativ til, og etter, brukerens fødselsmåned.
 *         F.eks., for en bruker født i april (4), så skal startmåned 2 tolkes som førstkommende juni (6 = 4 + 2)
 *         etter at brukeren har fylt startalder. Dette kan bety en reell dato i året etter at brukeren har fylt startalder.
 *
 */
data class NorskPensjonAlderDto(val aar: Int, val maaned: Int) {
    init {
        require(aar in 0..200) { "0 <= aar <= 200" }
        require(maaned in 1..12) { "1 <= maaned <= 12" }
    }
}
