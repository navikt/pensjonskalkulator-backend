package no.nav.pensjon.kalkulator.simulering.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.EpsSpec
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.LevendeEps
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import no.nav.pensjon.kalkulator.simulering.api.dto.*

class AnonymSimuleringSpecMapperV1Test : ShouldSpec({

    should("map DTO to domain") {
        val dto = AnonymSimuleringSpecV1(
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
                uttaksalder = AnonymSimuleringAlderV1(aar = 67, maaneder = 0),
                aarligInntektVsaPensjonBeloep = 200000
            ),
            heltUttak = AnonymSimuleringHeltUttakV1(
                uttaksalder = AnonymSimuleringAlderV1(aar = 70, maaneder = 0),
                aarligInntektVsaPensjon = AnonymSimuleringInntektV1(beloep = 0, sluttAlder = null)
            )
        )

        AnonymSimuleringSpecMapperV1.fromAnonymSimuleringSpecV1(dto) shouldBe
                ImpersonalSimuleringSpec(
                    simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
                    eps = EpsSpec(levende = LevendeEps(harInntektOver2G = true, harPensjon = false)),
                    forventetAarligInntektFoerUttak = 400000,
                    sivilstand = Sivilstand.SAMBOER,
                    gradertUttak = GradertUttak(
                        grad = Uttaksgrad.FEMTI_PROSENT,
                        uttakFomAlder = Alder(aar = 67, maaneder = 0),
                        aarligInntekt = 200000
                    ),
                    heltUttak = HeltUttak(
                        uttakFomAlder = Alder(aar = 70, maaneder = 0),
                        inntekt = Inntekt(
                            aarligBeloep = 0,
                            tomAlder = HeltUttak.defaultHeltUttakInntektTomAlder
                        )
                    ),
                    utenlandsopphold = Utenlandsopphold(periodeListe = emptyList(), antallAar = 5),
                    foedselAar = 1980,
                    inntektOver1GAntallAar = 30
                )
    }
})
