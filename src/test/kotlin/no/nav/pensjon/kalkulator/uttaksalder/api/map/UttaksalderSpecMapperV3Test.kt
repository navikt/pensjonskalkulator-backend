package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderAlderSpecV3
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderInntektSpecV3
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderSpecV3
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderUtenlandsperiodeSpecV3
import java.time.LocalDate

class UttaksalderSpecMapperV3Test : ShouldSpec({

    should("map data transfer object to domain object") {
        UttaksalderSpecMapperV3.fromDtoV3(
            UttaksalderSpecV3(
                simuleringstype = SimuleringType.ALDERSPENSJON,
                aarligInntektFoerUttakBeloep = 123,
                aarligInntektVsaPensjon = UttaksalderInntektSpecV3(
                    beloep = 456,
                    sluttAlder = UttaksalderAlderSpecV3(aar = 70, maaneder = 2)
                ),
                utenlandsperiodeListe = listOf(
                    UttaksalderUtenlandsperiodeSpecV3(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        landkode = "AUS",
                        arbeidetUtenlands = true
                    )
                ),
                sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
                epsHarInntektOver2G = true,
                epsHarPensjon = true
            )
        ) shouldBe
                ImpersonalUttaksalderSpec(
                    simuleringType = SimuleringType.ALDERSPENSJON,
                    sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
                    harEps = true,
                    aarligInntektFoerUttak = 123,
                    gradertUttak = null,
                    heltUttak = HeltUttak(
                        uttakFomAlder = null,
                        inntekt = Inntekt(
                            aarligBeloep = 456,
                            tomAlder = Alder(aar = 70, maaneder = 2)
                        )
                    ),
                    utenlandsperiodeListe = listOf(
                        Opphold(
                            fom = LocalDate.of(1990, 1, 2),
                            tom = LocalDate.of(1999, 11, 30),
                            land = Land.AUS,
                            arbeidet = true
                        )
                    )
                )
    }
})
