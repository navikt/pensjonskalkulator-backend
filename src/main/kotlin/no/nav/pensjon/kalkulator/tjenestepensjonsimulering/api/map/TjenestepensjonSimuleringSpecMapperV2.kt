package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.IngressSimuleringOffentligTjenestepensjonSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.FremtidigInntektV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.tpsimulering.SimuleringOffentligTjenestepensjonSpecV2
import java.time.LocalDate

object TjenestepensjonSimuleringSpecMapperV2 {

    fun fromDtoV2(spec: IngressSimuleringOffentligTjenestepensjonSpecV2): SimuleringOffentligTjenestepensjonSpecV2 {
        return SimuleringOffentligTjenestepensjonSpecV2(
            foedselsdato = spec.foedselsdato,
            uttaksdato = uttakDato(spec.foedselsdato, mapToUttaksalder(spec)),
            sisteInntekt = spec.aarligInntektFoerUttakBeloep,
            fremtidigeInntekter = mapToFremtidigInntektV2(spec),
            aarIUtlandetEtter16 = antallAar(spec.utenlandsperiodeListe),
            brukerBaOmAfp = spec.brukerBaOmAfp,
            epsPensjon = spec.epsHarPensjon,
            eps2G = spec.epsHarInntektOver2G,
            erApoteker = spec.erApoteker ?: false
        )
    }

    private fun mapToFremtidigInntektV2(
        spec: IngressSimuleringOffentligTjenestepensjonSpecV2,
    ): List<FremtidigInntektV2> {
        val inntekter: MutableList<FremtidigInntektV2> = mutableListOf()
        spec.gradertUttak?.aarligInntektVsaPensjonBeloep?.let {
            inntekter.add(
                FremtidigInntektV2(
                    fom = uttakDato(
                        spec.foedselsdato,
                        Alder(spec.gradertUttak.uttaksalder.aar, spec.gradertUttak.uttaksalder.maaneder)
                    ),
                    beloep = it
                )
            )
        }

        spec.heltUttak.aarligInntektVsaPensjon?.let {
            inntekter.add(
                FremtidigInntektV2(
                    fom = uttakDato(
                        spec.foedselsdato,
                        Alder(spec.heltUttak.uttaksalder.aar, spec.heltUttak.uttaksalder.maaneder)
                    ),
                    beloep = it.beloep
                )
            )
            inntekter.add(
                FremtidigInntektV2(
                    fom = uttakDato(
                        spec.foedselsdato,
                        Alder(it.sluttAlder.aar, it.sluttAlder.maaneder)
                    ).plusMonths(1), //sluttAlder er tom, '0' inntekt starter en måned etter
                    beloep = 0
                )
            )
        } ?: inntekter.add(
            FremtidigInntektV2(
                fom = uttakDato(
                    spec.foedselsdato,
                    if (inntekter.isEmpty()){
                        mapToUttaksalder(spec) //0-inntekt starter ved gradert uttaksdato med mindre helt uttak er spesifisert, da starter 0-inntekt ved helt uttaksdato
                    }
                    else { // gradert uttak er spesifisert med inntekt ved siden av, 0-inntekt starter ved helt uttaksdato
                        Alder(spec.heltUttak.uttaksalder.aar, spec.heltUttak.uttaksalder.maaneder)
                    }
                ),
                beloep = 0
            )
        )

        return inntekter
    }

    private fun mapToUttaksalder(spec: IngressSimuleringOffentligTjenestepensjonSpecV2): Alder {
        return (spec.gradertUttak?.uttaksalder ?: spec.heltUttak.uttaksalder)
            .let { Alder(it.aar, it.maaneder) }
    }

    private fun antallAar(oppholdListe: List<UtenlandsoppholdV2>): Int {
        val dagensDato = LocalDate.now()
        val sammenslattePerioder = slaaSammenOverlappendePerioder(oppholdListe, dagensDato)
        val antallDager = antallDager(sammenslattePerioder, dagensDato)
        return (antallDager / DAGER_PER_AAR).toInt()
    }

    private fun antallDager(oppholdListe: List<UtenlandsoppholdV2>, dagensDato: LocalDate): Long {
        return oppholdListe.sumOf { (it.tom ?: dagensDato).toEpochDay() + 1 - it.fom.toEpochDay() }
    }

    private fun slaaSammenOverlappendePerioder(oppholdListe: List<UtenlandsoppholdV2>, dagensDato: LocalDate): List<UtenlandsoppholdV2> {
        if (oppholdListe.isEmpty()) return emptyList()
        val sortertePerioder = oppholdListe.sortedBy { it.fom }

        val sammenslaatte = mutableListOf<UtenlandsoppholdV2>()
        var gjeldendePeriode = sortertePerioder.first()

        for (periode in sortertePerioder.drop(1)) {
            if (periode.fom <= (gjeldendePeriode.tom ?: dagensDato).plusDays(1)) {
                // Slå sammen perioder hvis de overlapper eller henger sammen
                gjeldendePeriode = UtenlandsoppholdV2(
                    fom = gjeldendePeriode.fom,
                    tom = maxOf(gjeldendePeriode.tom ?: dagensDato, periode.tom ?: dagensDato)
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