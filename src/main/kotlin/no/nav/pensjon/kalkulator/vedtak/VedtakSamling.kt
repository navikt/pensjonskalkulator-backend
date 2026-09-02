package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.math.BigDecimal
import java.time.LocalDate

data class VedtakSamling(
    val loependeAlderspensjon: LoependeAlderspensjon?,
    val fremtidigAlderspensjon: FremtidigAlderspensjon?,
    val privatAfp: LoependeEntitet?,
    val tidsbegrensetOffentligAfp: LoependeEntitet? = null,
    val gjenlevenderett: Gjenlevenderett? = null,
    val ufoeretrygd: LoependeUfoeretrygd?,
    val avdoed: InformasjonOmAvdoed?,
    val harGjenlevenderett: Boolean? = null
) {
    fun withAlderspensjonUtbetalingSisteMaaned(utbetaling: Utbetaling) =
        copy(loependeAlderspensjon = loependeAlderspensjon?.withUtbetalingSisteMaaned(utbetaling))

    fun withGjenlevenderett(harGjenlevenderett: Boolean?) =
        copy(harGjenlevenderett = harGjenlevenderett)

    fun medAvdoedNavn(navn: Navn) =
        copy(gjenlevenderett = gjenlevenderett?.medNavn(navn))

    fun hasContent(): Boolean =
        loependeAlderspensjon != null
                || fremtidigAlderspensjon != null
                || ufoeretrygd != null
                || privatAfp != null
                || tidsbegrensetOffentligAfp != null
                || gjenlevenderett != null
    // Informasjon om avdød regnes ikke som "content" i form av vedtak
}

data class LoependeAlderspensjon(
    val grad: Uttaksgrad,
    val fom: LocalDate,
    val uttaksgradFom: LocalDate? = null,
    val utbetalingSisteMaaned: Utbetaling? = null,
    val sivilstatus: Sivilstatus,
    val harGjenlevenderett: Boolean,
    val harUtenlandsopphold: Boolean
) {
    fun withUtbetalingSisteMaaned(utbetaling: Utbetaling) =
        copy(utbetalingSisteMaaned = utbetaling)
}

data class FremtidigAlderspensjon(
    val grad: Uttaksgrad,
    val fom: LocalDate,
    val sivilstatus: Sivilstatus
)

data class Gjenlevenderett(
    val avdoedPid: Pid,
    val doedsdato: LocalDate?,
    val foersteVirkningsdato: LocalDate?,
    val navn: Navn? = null
){
    fun medNavn(navn: Navn) =
        copy(navn = navn)
}

data class LoependeUfoeretrygd(
    val grad: Int,
    val fom: LocalDate
)

data class LoependeEntitet(
    val fom: LocalDate
)

data class Utbetaling(
    val beloep: BigDecimal?,
    val posteringsdato: LocalDate
)

data class InformasjonOmAvdoed(
    val pid: Pid?,
    val doedsdato: LocalDate?,
    val foersteAlderspensjonVirkningsdato: LocalDate?,
    val aarligPensjonsgivendeInntektErMinst1G: Boolean?,
    val harTilstrekkeligMedlemskapIFolketrygden: Boolean?,
    val antallAarUtenlands: Int?,
    val erFlyktning: Boolean?
)