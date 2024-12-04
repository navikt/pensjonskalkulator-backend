package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Inntekt
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderAlderSpecV2
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderInntektSpecV2
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderSpecV2
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderUtenlandsperiodeSpecV2
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UttaksalderSpecMapperV2Test {

    @Test
    fun `fromDtoV2 maps data transfer object to domain object`() {
        val domainObject: ImpersonalUttaksalderSpec = UttaksalderSpecMapperV2.fromDtoV2(
            UttaksalderSpecV2(
                simuleringstype = SimuleringType.ALDERSPENSJON,
                aarligInntektFoerUttakBeloep = 123,
                aarligInntektVsaPensjon = UttaksalderInntektSpecV2(
                    beloep = 456,
                    sluttAlder = UttaksalderAlderSpecV2(aar = 70, maaneder = 2)
                ),
                utenlandsperiodeListe = listOf(
                    UttaksalderUtenlandsperiodeSpecV2(
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
        )

        val expected = ImpersonalUttaksalderSpec(
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

        domainObject shouldBe expected
    }
}
