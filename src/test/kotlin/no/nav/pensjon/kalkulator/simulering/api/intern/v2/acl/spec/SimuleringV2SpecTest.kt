package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.EpsSpec
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.LevendeEps
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import java.time.LocalDate

class SimuleringV2SpecTest : ShouldSpec({

    should("mappe påkrevde felt og bruke default EPS-spesifikasjon") {
        SimuleringV2Spec(
            simuleringstype = SimuleringV2SimuleringstypeSpec.ALDERSPENSJON,
            heltUttak = SimuleringV2HeltUttakSpec(
                uttaksalder = SimuleringV2AlderSpec(aar = 65, maaneder = 7)
            )
        ).toInternalValue(
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

    should("mappe uttak") {
        val result = SimuleringV2Spec(
            simuleringstype = SimuleringV2SimuleringstypeSpec.ALDERSPENSJON,
            gradertUttak = SimuleringV2GradertUttakSpec(
                grad = 50,
                uttaksalder = SimuleringV2AlderSpec(aar = 62, maaneder = 3),
                aarligInntektVsaPensjonBeloep = 123000
            ),
            heltUttak = SimuleringV2HeltUttakSpec(
                uttaksalder = SimuleringV2AlderSpec(aar = 65, maaneder = 7),
                aarligInntektVsaPensjon = SimuleringV2InntektSpec(
                    beloep = 89000,
                    sluttAlder = SimuleringV2AlderSpec(aar = 70, maaneder = 11)
                )
            )
        ).toInternalValue(tillatSenereFoersteuttakForUfoere = true)

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

    should("mappe utenlandsopphold") {
        SimuleringV2Spec(
            simuleringstype = SimuleringV2SimuleringstypeSpec.ALDERSPENSJON,
            heltUttak = SimuleringV2HeltUttakSpec(
                uttaksalder = SimuleringV2AlderSpec(aar = 65, maaneder = 7)
            ),
            utenlandsperiodeListe = listOf(
                SimuleringV2UtenlandsperiodeSpec(
                    fom = LocalDate.of(1995, 2, 15),
                    tom = LocalDate.of(1999, 1, 10),
                    landkode = "ITA",
                    arbeidetUtenlands = true
                ),
                SimuleringV2UtenlandsperiodeSpec(
                    fom = LocalDate.of(2001, 1, 1),
                    tom = LocalDate.of(2001, 12, 31),
                    landkode = "AND",
                    arbeidetUtenlands = false
                )
            )
        ).toInternalValue(
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