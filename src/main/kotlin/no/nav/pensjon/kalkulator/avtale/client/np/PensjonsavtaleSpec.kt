package no.nav.pensjon.kalkulator.avtale.client.np

import no.nav.pensjon.kalkulator.person.Pid

/**
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
data class PensjonsavtaleSpec(
    val pid: Pid,
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpec>,
    val antallInntektsaarEtterUttak: Int,
    val harAfp: Boolean = false,
    val harEpsPensjon: Boolean = true, // Norsk Pensjon default
    val harEpsPensjonsgivendeInntektOver2G: Boolean = true, // Norsk Pensjon default
    val antallAarIUtlandetEtter16: Int = 0,
    val sivilstatus: Sivilstatus = Sivilstatus.GIFT, // Norsk Pensjon default
    val oenskesSimuleringAvFolketrygd: Boolean = false
)
