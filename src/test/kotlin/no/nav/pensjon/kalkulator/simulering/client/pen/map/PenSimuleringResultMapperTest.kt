package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAlderDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAlternativDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenSimuleringResultDto
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenVilkaarsproevingDto
import no.nav.pensjon.kalkulator.testutil.Assertions.assertAlder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PenSimuleringResultMapperTest {

    @Test
    fun `fromDto maps PEN-specific data transfer object to domain object`() {
        val resultat: SimuleringResult = PenSimuleringResultMapper.fromDto(
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
                ),
                harNokTrygdetidForGarantipensjon = false,
                opptjeningGrunnlagListe = emptyList()
            )
        )

        with(resultat) {
            assertTrue(alderspensjon.isEmpty())
            assertTrue(afpPrivat.isEmpty())
            assertTrue(harForLiteTrygdetid)
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
}
