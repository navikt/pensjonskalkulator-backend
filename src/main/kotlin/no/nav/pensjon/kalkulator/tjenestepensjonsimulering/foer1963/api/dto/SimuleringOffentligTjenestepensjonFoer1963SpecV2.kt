package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto

import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.api.dto.PersonligSimuleringGradertUttakSpecV8
import no.nav.pensjon.kalkulator.simulering.api.dto.PersonligSimuleringHeltUttakSpecV8
import no.nav.pensjon.kalkulator.simulering.api.dto.PersonligSimuleringUtenlandsperiodeSpecV8
import java.time.LocalDate
data class SimuleringOffentligTjenestepensjonFoer1963SpecV2(
    val simuleringstype: SimuleringType,
    val foedselsdato: LocalDate,
    val aarligInntektFoerUttakBeloep: Int?,
    val gradertUttak: PersonligSimuleringGradertUttakSpecV8? = null, // default is helt uttak (100 %)
    val heltUttak: PersonligSimuleringHeltUttakSpecV8,
    val utenlandsperiodeListe: List<PersonligSimuleringUtenlandsperiodeSpecV8>? = null,
    val sivilstand: Sivilstand?,
    val epsHarInntektOver2G: Boolean? = null,
    val epsHarPensjon: Boolean? = null,
    val afpInntektMaanedFoerUttak: Boolean?,
    val afpOrdning: AfpOrdningType? = null,
    val afpInntektMndForUttak: Int?,
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