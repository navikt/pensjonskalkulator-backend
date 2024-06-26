package no.nav.pensjon.kalkulator.ekskludering.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingAarsak
import no.nav.pensjon.kalkulator.ekskludering.EkskluderingStatus
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingAarsakV2
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV1
import no.nav.pensjon.kalkulator.ekskludering.api.dto.EkskluderingStatusV2
import org.junit.jupiter.api.Test

class EkskluderingMapperTest {

    @Test
    fun `'version1' maps to data transfer object version 1`() {
        val source = EkskluderingStatus(
            ekskludert = true,
            aarsak = EkskluderingAarsak.HAR_GJENLEVENDEYTELSE
        )
        val expected = EkskluderingStatusV1(
            ekskludert = true,
            aarsak = EkskluderingAarsakV1.HAR_GJENLEVENDEYTELSE
        )

        EkskluderingMapper.version1(source) shouldBe expected
    }

    @Test
    fun `'version2' maps to data transfer object version 2`() {
        val source = EkskluderingStatus(
            ekskludert = true,
            aarsak = EkskluderingAarsak.ER_APOTEKER
        )
        val expected = EkskluderingStatusV2(
            ekskludert = true,
            aarsak = EkskluderingAarsakV2.ER_APOTEKER
        )

        EkskluderingMapper.version2(source) shouldBe expected
    }

}
