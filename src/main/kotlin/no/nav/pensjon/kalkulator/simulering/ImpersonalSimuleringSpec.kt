package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Sivilstand
import java.time.LocalDate

data class ImpersonalSimuleringSpec(
    val simuleringType: SimuleringType,
    val uttaksgrad: Uttaksgrad,
    val foersteUttaksalder: Alder,
    val foedselsdato: LocalDate,
    val epsHarInntektOver2G: Boolean,
    val forventetInntekt: Int? = null,
    val sivilstand: Sivilstand? = null
) {
    val foersteUttaksdato: LocalDate = PensjonUtil.foersteUttaksdato(foedselsdato, foersteUttaksalder)
}
