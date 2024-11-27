package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.SimuleringOFTPSpec
import java.time.LocalDate

data class IngressSimuleringOFTPSpecV2 (
    val foedselsdato: LocalDate,
    val uttaksalder: Alder,
    val aarligInntektFoerUttakBeloep: Int,
    val antallAarIUtlandetEtter16: Int,
    val epsHarPensjon: Boolean,
    val epsHarInntektOver2G: Boolean,
    val brukerBaOmAfp: Boolean,
) {
    fun toSimuleringOFTPSpec(pid: Pid): SimuleringOFTPSpec {
        return SimuleringOFTPSpec(
            pid = pid.value,
            foedselsdato = foedselsdato,
            uttaksdato = uttakDato(foedselsdato, uttaksalder),
            sisteInntekt = aarligInntektFoerUttakBeloep,
            aarIUtlandetEtter16 = antallAarIUtlandetEtter16,
            brukerBaOmAfp = brukerBaOmAfp,
            epsPensjon = epsHarPensjon,
            eps2G = epsHarInntektOver2G
        )
    }
}