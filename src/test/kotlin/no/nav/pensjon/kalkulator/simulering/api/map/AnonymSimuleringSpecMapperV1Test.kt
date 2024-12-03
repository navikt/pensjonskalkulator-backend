package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.dto.*
import org.junit.jupiter.api.Test

class AnonymSimuleringSpecMapperV1Test {

    @Test
    fun `fromAnonymSimuleringSpecV1 maps DTO to domain`() {
        // Input DTO
        val inputDto = AnonymSimuleringSpecV1(
            simuleringstype = AnonymSimuleringTypeV1.ALDERSPENSJON_MED_AFP_PRIVAT,
            foedselAar = 1980,
            sivilstand = AnonymSivilstandV1.SAMBOER,
            epsHarInntektOver2G = true,
            epsHarPensjon = false,
            utenlandsAntallAar = 5,
            inntektOver1GAntallAar = 30,
            aarligInntektFoerUttakBeloep = 400000,
            gradertUttak = AnonymSimuleringGradertUttakV1(
                grad = 50,
                uttaksalder = AnonymSimuleringAlderV1(67, 0),
                aarligInntektVsaPensjonBeloep = 200000
            ),
            heltUttak = AnonymSimuleringHeltUttakV1(
                uttaksalder = AnonymSimuleringAlderV1(70, 0),
                aarligInntektVsaPensjon = AnonymSimuleringInntektV1(
                    beloep = 0,
                    sluttAlder = null
                )
            )
        )

        // Expected domain model
        val expectedDomainModel = ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            eps = Eps(harInntektOver2G = true, harPensjon = false),
            forventetAarligInntektFoerUttak = 400000,
            sivilstand = Sivilstand.SAMBOER,
            gradertUttak = GradertUttak(
                grad = Uttaksgrad.from(50),
                uttakFomAlder = Alder(67, 0),
                aarligInntekt = 200000
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(70, 0),
                inntekt = Inntekt(
                    aarligBeloep = 0,
                    tomAlder = HeltUttak.defaultHeltUttakInntektTomAlder
                )
            ),
            utenlandsopphold = Utenlandsopphold(
                periodeListe = emptyList(),
                antallAar = 5
            ),
            foedselAar = 1980,
            inntektOver1GAntallAar = 30
        )

        // Perform mapping
        val result = AnonymSimuleringSpecMapperV1.fromAnonymSimuleringSpecV1(inputDto)

        // Assert that the result matches the expected domain model
        result shouldBe expectedDomainModel
    }
}
