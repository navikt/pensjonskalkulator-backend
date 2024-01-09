package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.Alder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ImpersonalSimuleringSpecTest {

    /**
     * Første uttaksdato uten "måned-overflow" (måned-summen er 12 eller mindre) er datoen med:
     * - aar = foedselsaar + uttaksalder-aar
     * - maaned = foedselsmaaned + uttaksalder-maaned + 1
     * - dag = 1
     */
    @Test
    fun `foersteUttaksdato uten maaned-overflow`() {
        val spec = ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            foersteUttakAlder = Alder(67, 0),
            foedselDato = LocalDate.of(1963, 1, 1),
            epsHarInntektOver2G = false
        )

        assertEquals(LocalDate.of(2030, 2, 1),  spec.foersteUttakDato)
    }

    /**
     * Første uttaksdato med "måned-overflow" (måned-summen er større enn 12) er datoen med:
     * - aar = foedselsaar + uttaksalder-aar + 1
     * - maaned = foedselsmaaned + uttaksalder-maaned - 12 + 1
     * - dag = 1
     */
    @Test
    fun `foersteUttaksdato med maaned-overflow`() {
        val spec = ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            foersteUttakAlder = Alder(62, 11),
            foedselDato = LocalDate.of(1963, 12, 31),
            epsHarInntektOver2G = false
        )

        assertEquals(LocalDate.of(2026, 12, 1),  spec.foersteUttakDato)
    }
}
