package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

data class IngressSimuleringOFTPSpecV1 (
    val foedselsdato: LocalDate,
    val uttaksalder: Alder,
    val aarligInntektFoerUttakBeloep: Int,
    val antallAarIUtlandetEtter16: Int,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val brukerBaOmAfpOffentlig: Boolean,
)