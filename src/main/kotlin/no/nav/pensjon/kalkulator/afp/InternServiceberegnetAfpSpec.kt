package no.nav.pensjon.kalkulator.afp

import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.simulering.Opphold
import java.time.LocalDate

/**
 * Mellomliggende representasjon av spesifikasjon for simulering av serviceberegnet AFP.
 * Forskjellen mellom denne og ServiceberegnetAfpSpec er at her
 * er inntekter angitt som to enkeltbeløp (inntektForrigeKalenderaar og inntektFremTilUttak),
 * mens i ServiceberegnetAfpSpec er inntekter angitt som en liste over årlig opptjening.
 */
data class InternServiceberegnetAfpSpec(
    val fodselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val afpOrdning: String,
    val flyktning: Boolean?,
    val antAarIUtlandet: Int?,
    val utenlandsopphold: List<Opphold>?,
    val forventetArbeidsinntekt: Int?,
    val inntektMndForAfp: Int?,
    val inntektForrigeKalenderaar: Int? = null,
    val inntektFremTilUttak: Int? = null,
    val epsMottarPensjon: Boolean? = null,
    val epsInntektOver2G: Boolean? = null,
    val sivilstatus: Sivilstatus? = null
)