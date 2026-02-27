package no.nav.pensjon.kalkulator.opptjening.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.opptjening.api.dto.InntektDto
import java.math.BigDecimal

class FremtidigInntektV2MapperTest : ShouldSpec({

    should("map år and beløp") {
        InntektMapper.toDto(
            Inntekt(
                type = Opptjeningstype.OTHER,
                aar = 2023,
                beloep = BigDecimal.TEN
            )
        ) shouldBe InntektDto(aar = 2023, beloep = 10)
    }

    should("map decimal beløp to integer, rounding down") {
        InntektMapper.toDto(
            Inntekt(
                type = Opptjeningstype.OTHER,
                aar = 2023,
                beloep = BigDecimal("67.89")
            )
        ).beloep shouldBe 67
    }
})
