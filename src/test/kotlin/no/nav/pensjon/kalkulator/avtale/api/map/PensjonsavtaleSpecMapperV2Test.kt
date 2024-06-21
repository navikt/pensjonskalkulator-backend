package no.nav.pensjon.kalkulator.avtale.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.InntektSpec
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleAlderSpecV2
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleInntektSpecV2
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecV2
import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleUttaksperiodeSpecV2
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Test

class PensjonsavtaleSpecMapperV2Test {

    @Test
    fun `fromDtoV2 maps version 2 of data transfer object to domain object (pensjonsavtale specification)`() {
        PensjonsavtaleSpecMapperV2.fromDtoV2(
            PensjonsavtaleSpecV2(
                aarligInntektFoerUttakBeloep = 234000,
                uttaksperioder = listOf(
                    PensjonsavtaleUttaksperiodeSpecV2(
                        startAlder = PensjonsavtaleAlderSpecV2(aar = 68, maaneder = 0),
                        grad = 50,
                        aarligInntektVsaPensjon = PensjonsavtaleInntektSpecV2(
                            beloep = 123000,
                            sluttAlder = PensjonsavtaleAlderSpecV2(aar = 75, maaneder = 10)
                        )
                    )
                ),
                harAfp = true, // this value will be ignored in mapping
                harEpsPensjon = true,
                harEpsPensjonsgivendeInntektOver2G = true,
                antallAarIUtlandetEtter16 = 5,
                sivilstand = Sivilstand.SEPARERT_PARTNER
            )
        ) shouldBe
                PensjonsavtaleSpec(
                    aarligInntektFoerUttak = 234000,
                    uttaksperioder = listOf(
                        UttaksperiodeSpec(
                            startAlder = Alder(aar = 68, maaneder = 0),
                            grad = Uttaksgrad.FEMTI_PROSENT,
                            aarligInntekt = InntektSpec(
                                aarligBeloep = 123000,
                                tomAlder = Alder(aar = 75, maaneder = 10)
                            )
                        )
                    ),
                    harEpsPensjon = true,
                    harEpsPensjonsgivendeInntektOver2G = true,
                    antallAarIUtlandetEtter16 = 5,
                    sivilstand = Sivilstand.SEPARERT_PARTNER
                )
    }
}
