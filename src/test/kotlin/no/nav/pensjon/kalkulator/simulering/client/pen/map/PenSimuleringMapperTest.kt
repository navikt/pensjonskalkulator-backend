package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*
import no.nav.pensjon.kalkulator.testutil.Assertions.assertAlder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PenSimuleringMapperTest {

    @Test
    fun `toDto maps domain object to PEN-specific data transfer object`() {
        val dto: SimuleringEgressSpecDto = PenSimuleringMapper.toDto(impersonalSpec(), personalSpec())

        with(dto) {
            assertEquals("ALDER", simuleringstype)
            assertEquals("12906498357", pid)
            assertEquals("UGIF", sivilstand)
            assertTrue(epsHarInntektOver2G)
            assertFalse(epsHarPensjon)
            assertEquals(100_000, sisteInntekt)
            assertEquals(1, uttaksar)
            with(gradertUttak!!) {
                assertEquals(AlderSpecDto(67, 1), uttakFomAlder)
                assertEquals("P_80", grad)
                assertEquals(12_000, aarligInntekt)
            }
            with(heltUttak) {
                assertEquals(AlderSpecDto(68, 11), uttakFomAlder)
                assertEquals(6_000, aarligInntekt)
                assertEquals(AlderSpecDto(70, 3), inntektTomAlder)
            }
        }
    }

    @Test
    fun `fromDto maps PEN-specific data transfer object to domain object`() {
        val resultat: Simuleringsresultat = PenSimuleringMapper.fromDto(
            PenSimuleringResultDto(
                alderspensjon = emptyList(),
                afpPrivat = emptyList(),
                afpOffentliglivsvarig = emptyList(),
                vilkaarsproeving = PenVilkaarsproevingDto(
                    vilkaarErOppfylt = false,
                    alternativ = PenAlternativDto(
                        gradertUttaksalder = null,
                        uttaksgrad = null,
                        heltUttaksalder = PenAlderDto(aar = 65, maaneder = 4)
                    )
                )
            )
        )

        with(resultat) {
            assertTrue(alderspensjon.isEmpty())
            assertTrue(afpPrivat.isEmpty())
            with(vilkaarsproeving) {
                assertFalse(innvilget)
                with(alternativ!!) {
                    assertNull(gradertUttakAlder)
                    assertNull(uttakGrad)
                    assertAlder(65, 4, heltUttakAlder)
                }
            }
        }
    }

    private companion object {

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                epsHarInntektOver2G = true,
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.AATTI_PROSENT,
                    uttakFomAlder = Alder(67, 1),
                    aarligInntekt = 12_000
                ),
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(68, 11),
                    inntekt = Inntekt(
                        aarligBeloep = 6_000,
                        tomAlder = Alder(70, 3)
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
