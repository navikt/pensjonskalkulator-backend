package no.nav.pensjon.kalkulator.afp.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.time.LocalDate

data class InternServiceberegnetAfpSpec(
    val fodselsdato: LocalDate,
    val uttaksdato: LocalDate,
    val afpOrdning: String,
    val flyktning: Boolean?,
    val antAarIUtlandet: Int?,
    val forventetArbeidsinntekt: Int?,
    val inntektMndForAfp: Int?,
    val inntektForrigeKalenderaar: Int? = null,
    val inntektFremTilUttak: Int? = null,
    val epsMottarPensjon: Boolean? = null,
    val epsInntektOver2G: Boolean? = null,
    val sivilstatus: Sivilstatus? = null,
)