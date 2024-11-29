package no.nav.pensjon.kalkulator.avtale.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.avtale.InntektSpec
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.UttaksperiodeSpec
import no.nav.pensjon.kalkulator.avtale.api.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Sivilstand
import org.junit.jupiter.api.Test

class PensjonsavtaleSpecMapperV3Test {

    @Test
    fun `'fromDtoV3' maps version 3 of data transfer object to domain object (pensjonsavtale specification)`() {
        PensjonsavtaleSpecMapperV3.fromDtoV3(
            PensjonsavtaleSpecV3(
                aarligInntektFoerUttakBeloep = 234000,
                uttaksperioder = listOf(
                    PensjonsavtaleUttaksperiodeSpecV3(
                        startAlder = PensjonsavtaleAlderSpecV3(aar = 68, maaneder = 0),
                        grad = 50,
                        aarligInntektVsaPensjon = PensjonsavtaleInntektSpecV3(
                            beloep = 123000,
                            sluttAlder = PensjonsavtaleAlderSpecV3(aar = 75, maaneder = 10)
                        )
                    )
                ),
                harAfp = true, // this value will be ignored in mapping
                epsHarPensjon = true,
                epsHarInntektOver2G = true,
                sivilstand = PensjonsavtaleSivilstandSpecV3.SEPARERT_PARTNER
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
                    sivilstand = Sivilstand.SEPARERT_PARTNER
                )
    }

    @Test
    fun `'fromDtoV3' maps utenlandsperioder to aar`() {
        PensjonsavtaleSpecMapperV3.fromDtoV3(
            PensjonsavtaleSpecV3(
                aarligInntektFoerUttakBeloep = -1,
                uttaksperioder = emptyList()
            )
        ) shouldBe
                PensjonsavtaleSpec(
                    aarligInntektFoerUttak = -1,
                    uttaksperioder = emptyList(),
                    harEpsPensjon = null,
                    harEpsPensjonsgivendeInntektOver2G = null,
                    sivilstand = null
                )
    }
}
