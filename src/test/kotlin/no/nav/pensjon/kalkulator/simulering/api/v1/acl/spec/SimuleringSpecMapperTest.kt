package no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.*
import java.time.LocalDate

class SimuleringSpecMapperTest : ShouldSpec({

    should("map required fields and use default EPS specification") {
        SimuleringSpecMapper.fromDto(
            source = SimuleringV1Spec(
                simuleringstype = SimuleringV1SimuleringstypeSpec.ALDERSPENSJON,
                heltUttak = SimuleringV1HeltUttakSpec(
                    uttaksalder = SimuleringV1AlderSpec(aar = 65, maaneder = 7)
                )
            ),
            tillatSenereFoersteuttakForUfoere = false
        ) shouldBe ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            eps = EpsSpec(
                levende = LevendeEps(harInntektOver2G = false, harPensjon = false)
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 65, maaneder = 7),
                inntekt = null
            ),
            utenlandsopphold = Utenlandsopphold(periodeListe = emptyList()),
            tillatSenereFoersteuttakForUfoere = false
        )
    }

    should("map uttak") {
        val result = SimuleringSpecMapper.fromDto(
            source = SimuleringV1Spec(
                simuleringstype = SimuleringV1SimuleringstypeSpec.ALDERSPENSJON,
                gradertUttak = SimuleringV1GradertUttakSpec(
                    grad = 50,
                    uttaksalder = SimuleringV1AlderSpec(aar = 62, maaneder = 3),
                    aarligInntektVsaPensjonBeloep = 123000
                ),
                heltUttak = SimuleringV1HeltUttakSpec(
                    uttaksalder = SimuleringV1AlderSpec(aar = 65, maaneder = 7),
                    aarligInntektVsaPensjon = SimuleringV1InntektSpec(
                        beloep = 89000,
                        sluttAlder = SimuleringV1AlderSpec(aar = 70, maaneder = 11)
                    )
                )
            ),
            tillatSenereFoersteuttakForUfoere = true
        )

        with(result) {
            gradertUttak shouldBe GradertUttak(
                grad = Uttaksgrad.FEMTI_PROSENT,
                uttakFomAlder = Alder(aar = 62, maaneder = 3),
                aarligInntekt = 123000
            )
            heltUttak shouldBe HeltUttak(
                uttakFomAlder = Alder(aar = 65, maaneder = 7),
                inntekt = Inntekt(
                    aarligBeloep = 89000,
                    tomAlder = Alder(aar = 70, maaneder = 11)
                )
            )
            tillatSenereFoersteuttakForUfoere shouldBe true
        }
    }

    should("map utenlandsopphold") {
        SimuleringSpecMapper.fromDto(
            source = SimuleringV1Spec(
                simuleringstype = SimuleringV1SimuleringstypeSpec.ALDERSPENSJON,
                heltUttak = SimuleringV1HeltUttakSpec(
                    uttaksalder = SimuleringV1AlderSpec(aar = 65, maaneder = 7)
                ),
                utenlandsperiodeListe = listOf(
                    SimuleringV1UtenlandsperiodeSpec(
                        fom = LocalDate.of(1995, 2, 15),
                        tom = LocalDate.of(1999, 1, 10),
                        landkode = "ITA",
                        arbeidetUtenlands = true
                    ),
                    SimuleringV1UtenlandsperiodeSpec(
                        fom = LocalDate.of(2001, 1, 1),
                        tom = LocalDate.of(2001, 12, 31),
                        landkode = "AND",
                        arbeidetUtenlands = false
                    )
                )
            ),
            tillatSenereFoersteuttakForUfoere = false
        ).utenlandsopphold shouldBe Utenlandsopphold(
            periodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1995, 2, 15),
                    tom = LocalDate.of(1999, 1, 10),
                    land = Land.ITA,
                    arbeidet = true
                ),
                Opphold(
                    fom = LocalDate.of(2001, 1, 1),
                    tom = LocalDate.of(2001, 12, 31),
                    land = Land.AND,
                    arbeidet = false
                )
            )
        )
    }
})