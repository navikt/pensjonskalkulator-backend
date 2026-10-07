package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.time.LocalDate

data class PensjonsavtaleSpec(
    val person: PersonSpec,
    val aarligInntektFoerUttak: Int,
    val uttaksperioder: List<UttaksperiodeSpec>,
    val harEpsPensjon: Boolean? = null,
    val harEpsPensjonsgivendeInntektOver2G: Boolean? = null,
    val sivilstatus: Sivilstatus? = null
)

data class PersonSpec(
    val pid: Pid,
    val foedselsdato: LocalDate,
)

data class UttaksperiodeSpec(
    val startAlder: Alder,
    val grad: Uttaksgrad,
    val aarligInntekt: InntektSpec?,
)

data class InntektSpec(
    val aarligBeloep: Int,
    val tomAlder: Alder? = null
)
