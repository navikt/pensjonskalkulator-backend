package no.nav.pensjon.kalkulator.sak.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.sak.api.dto.SakDto

class SakMapperTest : ShouldSpec({

    should("map harSak to harUfoeretrygdEllerGjenlevendeytelse") {
        SakMapper.toDto(harSak = false) shouldBe SakDto(harUfoeretrygdEllerGjenlevendeytelse = false)
        SakMapper.toDto(harSak = true) shouldBe SakDto(harUfoeretrygdEllerGjenlevendeytelse = true)
    }
})
