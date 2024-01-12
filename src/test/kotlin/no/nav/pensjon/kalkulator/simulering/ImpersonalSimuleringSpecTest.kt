package no.nav.pensjon.kalkulator.simulering

import no.nav.pensjon.kalkulator.general.*
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
    fun `foersteUttakDato uten maaned-overflow`() {
        val spec = ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            epsHarInntektOver2G = false,
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(67, 0),
                inntekt = null,
                foedselDato = LocalDate.of(1963, 1, 1)
            )
        )

        assertEquals(LocalDate.of(2030, 2, 1), spec.foersteUttakDato)
    }

    /**
     * Første uttaksdato med "måned-overflow" (måned-summen er større enn 12) er datoen med:
     * - aar = foedselsaar + uttaksalder-aar + 1
     * - maaned = foedselsmaaned + uttaksalder-maaned - 12 + 1
     * - dag = 1
     */
    @Test
    fun `foersteUttakDato med maaned-overflow`() {
        val spec = ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            epsHarInntektOver2G = false,
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(62, 11),
                inntekt = null,
                foedselDato = LocalDate.of(1963, 12, 31)
            )
        )

        // Expected: 1963/12 + 62/11 + 0/1 = 2026/12
        assertEquals(LocalDate.of(2026, 12, 1), spec.foersteUttakDato)
    }

    @Test
    fun `foersteUttakDato is obtained from gradert uttak if specified`() {
        val foedselDato = LocalDate.of(1963, 1, 1)

        val spec = ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            epsHarInntektOver2G = false,
            gradertUttak = GradertUttak(
                grad = Uttaksgrad.FOERTI_PROSENT,
                uttakFomAlder = Alder(63, 9),
                aarligInntekt = 0,
                foedselDato = foedselDato
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(67, 0),
                inntekt = Inntekt(10_000, Alder(74, 8)),
                foedselDato = foedselDato
            )
        )

        // Expected: 1963/1 + 63/9 + 0/1 = 2026/11
        assertEquals(LocalDate.of(2026, 11, 1), spec.foersteUttakDato)
    }
}
