package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOffentligTjenestepensjonSpecV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV1
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpec
import java.time.LocalDate

object TjenestepensjonSimuleringSpecMapperV1 {

    fun fromDto(spec: IngressSimuleringOffentligTjenestepensjonSpecV1) : SimuleringOffentligTjenestepensjonSpec {
        return SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = spec.foedselsdato,
            uttaksdato = uttakDato(spec.foedselsdato, spec.uttaksalder),
            sisteInntekt = spec.aarligInntektFoerUttakBeloep,
            aarIUtlandetEtter16 = antallAar(spec.utenlandsperiodeListe),
            brukerBaOmAfp = spec.brukerBaOmAfp,
            epsPensjon = spec.epsHarPensjon,
            eps2G = spec.epsHarInntektOver2G
        )
    }

    private fun antallAar(oppholdListe: List<UtenlandsoppholdV1>): Int {
        val sammenslattePerioder = slaaSammenOverlappendePerioder(oppholdListe)
        val antallDager = antallDager(sammenslattePerioder)
        return (antallDager / DAGER_PER_AAR).toInt()
    }

    private fun antallDager(oppholdListe: List<UtenlandsoppholdV1>): Long {
        return oppholdListe.sumOf { (it.tom ?: LocalDate.now()).toEpochDay() + 1 - it.fom.toEpochDay() }
    }

    private fun slaaSammenOverlappendePerioder(oppholdListe: List<UtenlandsoppholdV1>): List<UtenlandsoppholdV1> {
        if (oppholdListe.isEmpty()) return emptyList()
        val sortertePerioder = oppholdListe.sortedBy { it.fom }

        val sammenslaatte = mutableListOf<UtenlandsoppholdV1>()
        var gjeldendePeriode = sortertePerioder.first()

        for (periode in sortertePerioder.drop(1)) {
            if (periode.fom <= (gjeldendePeriode.tom ?: LocalDate.now()).plusDays(1)) {
                // Slå sammen perioder hvis de overlapper eller henger sammen
                gjeldendePeriode = UtenlandsoppholdV1(
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