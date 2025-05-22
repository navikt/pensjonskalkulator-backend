package no.nav.pensjon.kalkulator.normalder.client.pen.acl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import java.lang.RuntimeException

class PenNormalderResultMapperTest : FunSpec({

    test("'fromDto' should map DTO to domain object") {
        PenNormalderResultMapper.fromDto(
            PenNormalderResult(
                normertPensjonsalderListe = listOf(
                    PenNormertPensjonsalder(
                        aarskull = 1963,
                        aar = 67,
                        maaned = 0,
                        nedreAar = 62,
                        nedreMaaned = 0,
                        oevreAar = 75,
                        oevreMaaned = 0,
                        type = "FAST"
                    )
                ),
                message = null,
                aarskull = null
            )
        ) shouldBe listOf(
            Aldersgrenser(
                aarskull = 1963,
                nedreAlder = Alder(62, 0),
                normalder = Alder(67, 0),
                oevreAlder = Alder(75, 0),
                verdiStatus = VerdiStatus.FAST
            )
        )
    }

    test("'fromDto' should throw exception when no normertPensjonsalderListe") {
        shouldThrow<RuntimeException> {
            PenNormalderResultMapper.fromDto(
                PenNormalderResult(
                    normertPensjonsalderListe = null,
                    message = "feil",
                    aarskull = 1970
                )
            )
        }.message shouldBe "Normalder-feil for årskull 1970: feil"
    }
})
