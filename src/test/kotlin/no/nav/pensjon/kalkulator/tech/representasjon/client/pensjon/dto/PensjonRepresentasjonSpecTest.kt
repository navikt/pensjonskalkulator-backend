package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonSpec
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjonstype

class PensjonRepresentasjonSpecTest : ShouldSpec({

    context("from") {
        should("convert from internal domain representation to external representation (data transfer object)") {
            PensjonRepresentasjonSpec.from(
                source =
                    RepresentasjonSpec(
                        fullmaktsgiverPid = PossiblyEncryptedPid("kryptert.verdi"),
                        fullmektigPid = pid,
                        gyldigeRepresentasjonstyper = listOf(
                            Representasjonstype.PENSJON_LES,
                            Representasjonstype.VERGE_PENSJON_SKRIV
                        ),
                        inkluderRepresentertNavn = true
                    )
            ) shouldBe PensjonRepresentasjonSpec(
                representertPid = "kryptert.verdi",
                representantPid = pid.value,
                validRepresentasjonstyper = listOf("PENSJON_LES", "VERGE_PENSJON_SKRIV"),
                includeRepresentertNavn = true
            )
        }
    }
})