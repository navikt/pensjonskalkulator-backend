package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.HarLivsvarigOffentligAfpDtoV1

class HarLivsvarigOffentligAfpV1DtoMapperTest : ShouldSpec({

    should("map true to a DTO with harLivsvarigOffentligAfp=true") {
        HarLivsvarigOffentligAfpV1DtoMapper.toDto(true) shouldBe
                HarLivsvarigOffentligAfpDtoV1(harLivsvarigOffentligAfp = true)
    }

    should("map false to a DTO with harLivsvarigOffentligAfp=false") {
        HarLivsvarigOffentligAfpV1DtoMapper.toDto(false) shouldBe
                HarLivsvarigOffentligAfpDtoV1(harLivsvarigOffentligAfp = false)
    }
})
