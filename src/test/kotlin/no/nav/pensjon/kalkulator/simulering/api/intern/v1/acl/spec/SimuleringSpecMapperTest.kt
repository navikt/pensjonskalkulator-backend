package no.nav.pensjon.kalkulator.simulering.api.intern.v1.acl.spec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.EpsSpec
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.LevendeEps
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold

class SimuleringSpecMapperTest : ShouldSpec({

    should("map required fields and use default EPS specification") {
        SimuleringSpecMapper.fromDto(
            source = SimuleringSpecDto(
                simuleringstype = SimuleringstypeSpecDto.ALDERSPENSJON,
                heltUttak = HeltUttakSpecDto(
                    uttaksalder = AlderSpecDto(aar = 65, maaneder = 7)
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
