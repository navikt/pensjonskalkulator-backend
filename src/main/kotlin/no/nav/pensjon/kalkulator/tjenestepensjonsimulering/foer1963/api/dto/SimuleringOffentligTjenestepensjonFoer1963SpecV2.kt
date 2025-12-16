package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import java.time.LocalDate
data class SimuleringOffentligTjenestepensjonFoer1963SpecV2(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val aarligInntektFoerUttakBeloep: Int?,
    val gradertUttak: PersonligSimuleringGradertUttakSpecV9? = null, // default is helt uttak (100 %)
    val heltUttak: PersonligSimuleringHeltUttakSpecV9,
    val utenlandsperiodeListe: List<PersonligSimuleringUtenlandsperiodeSpecV9>? = null,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean? = null,
    val epsHarPensjon: Boolean? = null,
    val afpInntektMaanedFoerUttak: Boolean?,
    val afpOrdning: AfpOrdningType? = null,
    val stillingsprosentOffHeltUttak: String,
    val stillingsprosentOffGradertUttak: String?,
)
/*
data class SimuleringOffentligTjenestepensjonFoer1963SpecV2(
    val simuleringType: SimuleringType,
    val samtykke: Boolean,
    val forventetInntekt: Int,
    val antArInntektOverG: Int,
    val forsteUttakDato: String,
    val utg: String,
    val inntektUnderGradertUttak: Int?,
    val heltUttakDato: String,
    val inntektEtterHeltUttak: Int?,
    val antallArInntektEtterHeltUttak: Int?,
    val sivilstand: String,
    val utenlandsopphold: Int?,
    val sivilstatus: String,
    val epsPensjon: Boolean,
    val eps2G: Boolean,
    val afpOrdning: String?,
    val afpInntektMndForUttak: Int?,
    val stillingsprosentOffHeltUttak: String,
    val stillingsprosentOffGradertUttak: String?,
    val utenlandsperiodeForSimuleringList: List<UtenlandsoppholdV2>?,
    val fremtidigInntektList: List<FremtidigInntektDto>?
)
*/