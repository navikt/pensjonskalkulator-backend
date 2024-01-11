package no.nav.pensjon.kalkulator.uttaksalder.api.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.AlderIngressDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderGradertUttakIngressDto
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class UttaksalderMapperTest {

    @Test
    fun `toDto maps domain object to data transfer object`() {
        val dto = UttaksalderMapper.toDto(Alder(2024, 5))

        with(dto!!) {
            assertEquals(2024, aar)
            assertEquals(5, maaneder)
        }
    }

    @Test
    fun `fromIngressSpecDto maps data transfer object to domain object`() {
        val spec = UttaksalderMapper.fromIngressSpecDto(
            UttaksalderIngressSpecDto(
                sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
                harEps = true,
                sisteInntekt = 123,
                simuleringstype = SimuleringType.ALDERSPENSJON,
                gradertUttak = UttaksalderGradertUttakIngressDto(
                    uttaksgrad = 50,
                    inntektUnderGradertUttak = 456,
                    heltUttakAlder = AlderIngressDto(70, 2),
                    foedselsdato = LocalDate.of(1964, 5, 6)
                )
            )
        )

        with(spec) {
            assertEquals(Sivilstand.GJENLEVENDE_PARTNER, sivilstand)
            assertTrue(harEps!!)
            assertEquals(123, sisteInntekt)
            assertEquals(SimuleringType.ALDERSPENSJON, simuleringType)

            with(gradertUttak!!) {
                assertEquals(Uttaksgrad.FEMTI_PROSENT, grad)
                assertEquals(456, aarligInntekt)
                assertEquals(LocalDate.of(2034, 8, 1), uttakFomDato)
                assertEquals(LocalDate.of(1964, 5, 6), foedselDato)

                with(uttakFomAlder) {
                    assertEquals(70, aar)
                    assertEquals(2, maaneder)
                }
            }
        }
    }
}
