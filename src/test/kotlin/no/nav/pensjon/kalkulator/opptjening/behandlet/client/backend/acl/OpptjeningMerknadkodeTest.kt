package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.opptjening.behandlet.MerknadCode

class OpptjeningMerknadkodeTest : ShouldSpec({

    should("mappe kjente eksterne verdier til intern domeneverdi") {
        OpptjeningMerknadkode.internalValue(externalValue = "AFP") shouldBe MerknadCode.AFP
        OpptjeningMerknadkode.internalValue(externalValue = "REFORM") shouldBe MerknadCode.REFORM
        OpptjeningMerknadkode.internalValue(externalValue = "UFOREGRAD") shouldBe MerknadCode.UFOEREGRAD
        OpptjeningMerknadkode.internalValue(externalValue = "DAGPENGER") shouldBe MerknadCode.DAGPENGER
        OpptjeningMerknadkode.internalValue(externalValue = "FORSTEGANGSTJENESTE") shouldBe MerknadCode.FOERSTEGANGSTJENESTE
        OpptjeningMerknadkode.internalValue(externalValue = "OMSORGSOPPTJENING") shouldBe MerknadCode.OMSORGSOPPTJENING
        OpptjeningMerknadkode.internalValue(externalValue = "GRADERT_UTTAK") shouldBe MerknadCode.GRADERT_UTTAK
        OpptjeningMerknadkode.internalValue(externalValue = "HELT_UTTAK") shouldBe MerknadCode.HELT_UTTAK
        OpptjeningMerknadkode.internalValue(externalValue = "UTTAK_STOPPET") shouldBe MerknadCode.UTTAK_STOPPET
    }

    should("mappe ukjent verdi til 'unknown'") {
        OpptjeningMerknadkode.internalValue(externalValue = "FOO") shouldBe MerknadCode.UNKNOWN
    }

    should("mappe manglende verdi til 'none'") {
        OpptjeningMerknadkode.internalValue(externalValue = null) shouldBe MerknadCode.NONE
        OpptjeningMerknadkode.internalValue(externalValue = "") shouldBe MerknadCode.NONE
    }
})