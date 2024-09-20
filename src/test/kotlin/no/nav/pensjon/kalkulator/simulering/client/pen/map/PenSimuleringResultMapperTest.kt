package no.nav.pensjon.kalkulator.simulering.client.pen.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.simulering.Alternativ
import no.nav.pensjon.kalkulator.simulering.SimuleringResult
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjeningGrunnlag
import no.nav.pensjon.kalkulator.simulering.Vilkaarsproeving
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.*
import org.junit.jupiter.api.Test

class PenSimuleringResultMapperTest {

    @Test
    fun `fromDto maps PEN-specific data transfer object to domain object`() {
        PenSimuleringResultMapper.fromDto(
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
                trygdetid = 10,
                opptjeningGrunnlagListe = listOf(
                    PenOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntekt = 123000)
                )
            )
        ) shouldBe SimuleringResult(
            alderspensjon = emptyList(),
            afpPrivat = emptyList(),
            afpOffentlig = emptyList(),
            vilkaarsproeving = Vilkaarsproeving(
                innvilget = false,
                alternativ = Alternativ(
                    gradertUttakAlder = null,
                    uttakGrad = null,
                    heltUttakAlder = Alder(aar = 65, maaneder = 4)
                )
            ),
            harForLiteTrygdetid = true,
            trygdetid = 10,
            opptjeningGrunnlagListe = listOf(
                SimulertOpptjeningGrunnlag(aar = 2001, pensjonsgivendeInntektBeloep = 123000)
            )
        )
    }
}
