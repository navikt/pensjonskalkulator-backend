package no.nav.pensjon.kalkulator.merknad.client.opptjening.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.UttaksgradMerknad

class OpptjeningMerknadTest : ShouldSpec({

    context("funksjonen 'toInternalValue'") {
        should("konvertere eksternt bestemt objektstruktur til intern domenerepresentasjon") {
            OpptjeningMerknad(
                kode = "FORSTEGANGSTJENESTE",
                uttaksgrad = OpptjeningUttaksgrad(
                    grad = 50,
                    kalendermaanedFom = 6
                )
            ).toInternalValue() shouldBe Merknad(
                kode = MerknadCode.FOERSTEGANGSTJENESTE,
                uttaksgrad = UttaksgradMerknad(
                    grad = Uttaksgrad.FEMTI_PROSENT,
                    kalendermaanedFom = 6
                )
            )
        }
    }
})