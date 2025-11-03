package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.LoependeInntekt
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringOffentligTjenestepensjonSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.UtenlandsoppholdV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.api.dto.SimuleringOffentligTjenestepensjonAlderV2
import java.time.LocalDate

object TjenestepensjonSimuleringSpecMapperV2 {
    private const val DAGER_PER_AAR = 365

    fun fromDtoV2(spec: SimuleringOffentligTjenestepensjonSpecV2): SimuleringOffentligTjenestepensjonSpec {
        val uttaksdato = uttakDato(spec.foedselsdato, mapToUttaksalder(spec))
        return SimuleringOffentligTjenestepensjonSpec(
            foedselsdato = spec.foedselsdato,
            uttaksdato = uttaksdato,
            sisteInntekt = spec.aarligInntektFoerUttakBeloep,
            fremtidigeInntekter = mapToFremtidigInntektV2(spec),
            aarIUtlandetEtter16 = antallAar(spec.utenlandsperiodeListe, uttaksdato),
            brukerBaOmAfp = spec.brukerBaOmAfp,
            epsPensjon = spec.epsHarPensjon,
            eps2G = spec.epsHarInntektOver2G,
            erApoteker = spec.erApoteker ?: false
        )
    }

    private fun mapToFremtidigInntektV2(
        spec: SimuleringOffentligTjenestepensjonSpecV2,
    ): List<LoependeInntekt> {
        val inntekter: MutableList<LoependeInntekt> = mutableListOf()

        spec.gradertUttak?.aarligInntektVsaPensjonBeloep?.let {
            inntekter.add(
                LoependeInntekt(
                    fom = uttakDato(
                        spec.foedselsdato,
                        mapToAlder(spec.gradertUttak.uttaksalder)
                    ),
                    beloep = it
                )
            )
        }

        spec.heltUttak.aarligInntektVsaPensjon?.let {
            inntekter.add(
                LoependeInntekt(
                    fom = uttakDato(
                        spec.foedselsdato,
                        mapToAlder(spec.heltUttak.uttaksalder)
                    ),
                    beloep = it.beloep
                )
            )
            inntekter.add(
                LoependeInntekt(
                    fom = uttakDato(
                        spec.foedselsdato,
                        mapToAlder(it.sluttAlder)
                    ).plusMonths(1), //sluttAlder er tom, '0' inntekt starter en måned etter
                    beloep = 0
                )
            )
        } ?: inntekter.add(
            LoependeInntekt(
                fom = uttakDato(
                    foedselDato = spec.foedselsdato,
                    uttakAlder = if (inntekter.isEmpty())
                        mapToUttaksalder(spec) //0-inntekt starter ved gradert uttaksdato med mindre helt uttak er spesifisert, da starter 0-inntekt ved helt uttaksdato
                    else // gradert uttak er spesifisert med inntekt ved siden av, 0-inntekt starter ved helt uttaksdato
                        mapToAlder(spec.heltUttak.uttaksalder)
                ),
                beloep = 0
            )
        )

        return inntekter
    }

    private fun mapToAlder(source: SimuleringOffentligTjenestepensjonAlderV2) =
        Alder(source.aar, source.maaneder)

    private fun mapToUttaksalder(spec: SimuleringOffentligTjenestepensjonSpecV2): Alder =
        (spec.gradertUttak?.uttaksalder ?: spec.heltUttak.uttaksalder).let(TjenestepensjonSimuleringSpecMapperV2::mapToAlder)

    private fun antallAar(oppholdListe: List<UtenlandsoppholdV2>, uttaksdato: LocalDate): Int {
        val sammenslattePerioder = slaaSammenOverlappendePerioder(oppholdListe, uttaksdato)
        val antallDager = antallDager(sammenslattePerioder, uttaksdato)
        return (antallDager / DAGER_PER_AAR).toInt()
    }

    private fun antallDager(oppholdListe: List<UtenlandsoppholdV2>, maksTomDato: LocalDate): Long =
        oppholdListe.sumOf { (it.tom ?: maksTomDato).toEpochDay() + 1 - it.fom.toEpochDay() }

    private fun slaaSammenOverlappendePerioder(
        oppholdListe: List<UtenlandsoppholdV2>,
        maksTomDato: LocalDate
    ): List<UtenlandsoppholdV2> {
        if (oppholdListe.isEmpty()) return emptyList()
        val sortertePerioder = oppholdListe.sortedBy { it.fom }

        val sammenslaatte = mutableListOf<UtenlandsoppholdV2>()
        var gjeldendePeriode = sortertePerioder.first()

        for (periode in sortertePerioder.drop(1)) {
            if (periode.fom <= (gjeldendePeriode.tom ?: maksTomDato).plusDays(1)) {
                // Slå sammen perioder hvis de overlapper eller henger sammen
                gjeldendePeriode = UtenlandsoppholdV2(
                    fom = gjeldendePeriode.fom,
                    tom = maxOf(gjeldendePeriode.tom ?: maksTomDato, periode.tom ?: maksTomDato)
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
}
