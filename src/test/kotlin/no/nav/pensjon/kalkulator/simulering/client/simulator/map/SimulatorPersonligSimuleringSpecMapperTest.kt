package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAlderSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorSimuleringSpec
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SimulatorPersonligSimuleringSpecMapperTest {

    @Test
    fun `toDto maps domain object to simulator-specific data transfer object`() {
        val dto: SimulatorSimuleringSpec =
            SimulatorPersonligSimuleringSpecMapper.toDto(impersonalSpec(), personalSpec())

        with(dto) {
            assertEquals("ALDER", simuleringstype)
            assertEquals("12906498357", pid)
            assertEquals("UGIF", sivilstand)
            assertTrue(epsHarInntektOver2G ?: true)
            assertFalse(epsHarPensjon ?: false)
            assertEquals(100_000, sisteInntekt)
            assertEquals(1, uttaksar)
            with(gradertUttak!!) {
                assertEquals(SimulatorAlderSpec(aar = 67, maaneder = 1), uttakFomAlder)
                assertEquals("P_80", grad)
                assertEquals(12_000, aarligInntekt)
            }
            with(heltUttak) {
                assertEquals(SimulatorAlderSpec(aar = 68, maaneder = 11), uttakFomAlder)
                assertEquals(6_000, aarligInntekt)
                assertEquals(SimulatorAlderSpec(aar = 70, maaneder = 3), inntektTomAlder)
            }
        }
    }

    private companion object {

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                eps = Eps(harInntektOver2G = true, harPensjon = false),
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.AATTI_PROSENT,
                    uttakFomAlder = Alder(aar = 67, maaneder = 1),
                    aarligInntekt = 12_000
                ),
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(68, 11),
                    inntekt = Inntekt(
                        aarligBeloep = 6_000,
                        tomAlder = Alder(aar = 70, maaneder = 3)
                    )
                ),
                utenlandsopphold = Utenlandsopphold(
                    periodeListe = listOf(
                        Opphold(
                            fom = LocalDate.of(1990, 1, 2),
                            tom = LocalDate.of(1999, 11, 30),
                            land = Land.AUS,
                            arbeidet = true
                        )
                    )
                )
            )

        private fun personalSpec() =
            PersonalSimuleringSpec(
                pid = pid,
                aarligInntektFoerUttak = 100_000,
                sivilstand = Sivilstand.UGIFT
            )
    }
}
