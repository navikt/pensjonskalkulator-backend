package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import no.nav.pensjon.kalkulator.simulering.Eps
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import java.time.LocalDate

data class SimuleringOffentligTjenestepensjonFoer1963Spec(
    val simuleringType: SimuleringType,
    val sivilstand: Sivilstand? = null,
    val foedselsdato: LocalDate,
    val eps: Eps,
    val forventetAarligInntektFoerUttak: Int? = null,
    val gradertUttak: GradertUttak? = null,
    val heltUttak: HeltUttak,
    val utenlandsopphold: Utenlandsopphold,
    val afpInntektMaanedFoerUttak: Boolean? = null,
    val afpOrdning: AfpOrdningType? = null,
    val afpInntektMndForUttak: Boolean?,
    val stillingsprosentOffHeltUttak: String,
    val stillingsprosentOffGradertUttak: String?,
)
