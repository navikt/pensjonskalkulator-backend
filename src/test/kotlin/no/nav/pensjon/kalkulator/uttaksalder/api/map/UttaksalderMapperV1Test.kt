package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.uttaksalder.ImpersonalUttaksalderSpec
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UttaksalderMapperV1Test {

    @Test
    fun `toDto maps domain object to data transfer object`() {
        val dto = UttaksalderMapperV1.toDto(Alder(aar = 2024, maaneder = 5))

        with(dto!!) {
            assertEquals(2024, aar)
            assertEquals(5, maaneder)
        }
    }

    @Test
    fun `fromIngressSpecForHeltUttakV1 maps data transfer object to domain object`() {
        val domainObject: ImpersonalUttaksalderSpec = UttaksalderMapperV1.fromIngressSpecForHeltUttakV1(
            IngressUttaksalderSpecForHeltUttakV1(
                simuleringstype = SimuleringType.ALDERSPENSJON,
                sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
                harEps = true,
                aarligInntektFoerUttakBeloep = 123,
                aarligInntektVsaPensjon = IngressUttaksalderInntektV1(
                    beloep = 456,
                    sluttAlder = IngressUttaksalderAlderV1(aar = 70, maaneder = 2)
                ),
                utenlandsperiodeListe = listOf(
                    UttaksalderUtenlandsperiodeSpecV1(
                        fom = LocalDate.of(1990, 1, 2),
                        tom = LocalDate.of(1999, 11, 30),
                        landkode = "AUS",
                        arbeidetUtenlands = true
                    )
                )
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
