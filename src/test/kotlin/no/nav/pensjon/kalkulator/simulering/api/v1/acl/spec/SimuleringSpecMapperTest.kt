package no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.*

class SimuleringSpecMapperTest : ShouldSpec({

    should("map required fields and use default EPS specification") {
        SimuleringSpecMapper.fromDto(
            source = SimuleringV1Spec(
                simuleringstype = SimuleringV1SimuleringstypeSpec.ALDERSPENSJON,
                heltUttak = SimuleringV1HeltUttakSpec(
                    uttaksalder = SimuleringV1AlderSpec(aar = 65, maaneder = 7)
                )
            )
        ) shouldBe ImpersonalSimuleringSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            eps = EpsSpec(
                levende = LevendeEps(harInntektOver2G = false, harPensjon = false)
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 65, maaneder = 7),
                inntekt = null
            ),
            utenlandsopphold = Utenlandsopphold(periodeListe = emptyList())
        )
    }
})
