package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.LoependeInntekt
import no.nav.pensjon.kalkulator.simulering.PensjonUtil.uttakDato
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.SimuleringOffentligTjenestepensjonSpecV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.UtenlandsoppholdV2
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.SimuleringOffentligTjenestepensjonSpec
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.api.dto.SimuleringOffentligTjenestepensjonAlderV2
import java.time.LocalDate

object TjenestepensjonSimuleringSpecMapperV2 {
    private val log = KotlinLogging.logger {}
    private const val DAGER_PER_AAR = 365

    fun fromDtoV2(spec: SimuleringOffentligTjenestepensjonSpecV2) =
        SimuleringOffentligTjenestepensjonSpec(
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
        (spec.gradertUttak?.uttaksalder ?: spec.heltUttak.uttaksalder).let(::mapToAlder)

    private fun antallAar(oppholdListe: List<UtenlandsoppholdV2>): Int {
        val dagensDato = LocalDate.now()
        val sammenslattePerioder: List<UtenlandsoppholdV2> = slaaSammenOverlappendePerioder(oppholdListe, dagensDato)
        val antallDager = antallDager(sammenslattePerioder, dagensDato)
        val antallAar = (antallDager / DAGER_PER_AAR).toInt()
        log.info { "Fikk perioder: $oppholdListe, sammenslåtte perioder: $sammenslattePerioder, antall Dager: $antallDager, antall år: $antallAar" }
        return antallAar
    }

    private fun antallDager(oppholdListe: List<UtenlandsoppholdV2>, dagensDato: LocalDate): Long =
        oppholdListe.sumOf { (it.tom ?: dagensDato).toEpochDay() + 1 - it.fom.toEpochDay() }

    private fun slaaSammenOverlappendePerioder(
        oppholdListe: List<UtenlandsoppholdV2>,
        dagensDato: LocalDate
    ): List<UtenlandsoppholdV2> {
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
}
