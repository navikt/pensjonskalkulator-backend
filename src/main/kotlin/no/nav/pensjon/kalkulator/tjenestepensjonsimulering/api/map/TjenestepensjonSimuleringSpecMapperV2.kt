package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOFTPSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOFTPSpec
import java.time.LocalDate

object TjenestepensjonSimuleringSpecMapperV2 {

    fun fromDto(spec: IngressSimuleringOFTPSpecV2) : SimuleringOFTPSpec {
        return SimuleringOFTPSpec(
            foedselsdato = spec.foedselsdato,
            uttaksdato = uttakDato(spec.foedselsdato, spec.uttaksalder),
            sisteInntekt = spec.aarligInntektFoerUttakBeloep,
            aarIUtlandetEtter16 = antallAar(spec.utenlandsperiodeListe),
            brukerBaOmAfp = spec.brukerBaOmAfp,
            epsPensjon = spec.epsHarPensjon,
            eps2G = spec.epsHarInntektOver2G
        )
    }

    fun antallAar(oppholdListe: List<UtenlandsoppholdV2>): Int {
        val sammenslattePerioder = slaaSammenOverlappendePerioder(oppholdListe)
        val antallDager = antallDager(sammenslattePerioder)
        return (antallDager / DAGER_PER_AAR).toInt()
    }

    fun antallDager(oppholdListe: List<UtenlandsoppholdV2>): Long {
        return oppholdListe.sumOf { (it.tom ?: LocalDate.now()).toEpochDay() + 1 - it.fom.toEpochDay() }
    }

    fun slaaSammenOverlappendePerioder(oppholdListe: List<UtenlandsoppholdV2>): List<UtenlandsoppholdV2> {
        if (oppholdListe.isEmpty()) return emptyList()
        val sortertePerioder = oppholdListe.sortedBy { it.fom }

        val sammenslaatte = mutableListOf<UtenlandsoppholdV2>()
        var gjeldendePeriode = sortertePerioder.first()

        for (periode in sortertePerioder.drop(1)) {
            if (periode.fom <= (gjeldendePeriode.tom ?: LocalDate.now()).plusDays(1)) {
                // Slå sammen perioder hvis de overlapper eller henger sammen
                gjeldendePeriode = UtenlandsoppholdV2(
                    fom = gjeldendePeriode.fom,
                    tom = maxOf(gjeldendePeriode.tom ?: LocalDate.now(), periode.tom ?: LocalDate.now())
                )
            } else {
                // Legg til gjeldende periode og start ny
                sammenslaatte.add(gjeldendePeriode)
                gjeldendePeriode = periode
            }
        }

        // Legg til siste periode
        sammenslaatte.add(gjeldendePeriode)
        return sammenslaatte
    }

    private const val DAGER_PER_AAR = 365

}