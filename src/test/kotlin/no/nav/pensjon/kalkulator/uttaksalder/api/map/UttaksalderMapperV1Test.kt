package no.nav.pensjon.kalkulator.uttaksalder.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.UtenlandsperiodeSpec
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
                UtenlandsperiodeSpec(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidetUtenlands = true
                )
            )
        )

        domainObject shouldBe expected
    }

    @Test
    fun `fromIngressSpecForGradertUttakV1 maps data transfer object to domain object`() {
        val dto = IngressUttaksalderSpecForGradertUttakV1(
            simuleringstype = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
            harEps = true,
            aarligInntektFoerUttakBeloep = 100_000,
            gradertUttak = IngressUttaksalderGradertUttakV1(
                grad = 50,
                aarligInntektVsaPensjonBeloep = 50_000
            ),
            heltUttak = IngressUttaksalderHeltUttakV1(
                uttaksalder = IngressUttaksalderAlderV1(aar = 67, maaneder = 1),
                aarligInntektVsaPensjon = IngressUttaksalderInntektV1(
                    beloep = 25_000,
                    sluttAlder = IngressUttaksalderAlderV1(aar = 70, maaneder = 11)
                )
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

        val actual = UttaksalderMapperV1.fromIngressSpecForGradertUttakV1(dto)

        val expected = ImpersonalUttaksalderSpec(
            sivilstand = Sivilstand.GJENLEVENDE_PARTNER,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            gradertUttak = UttaksalderGradertUttak(
                grad = Uttaksgrad.FEMTI_PROSENT,
                aarligInntekt = 50_000,
                foedselDato = LocalDate.MIN // deprecated; irrelevant
            ),
            heltUttak = HeltUttak(
                uttakFomAlder = Alder(aar = 67, maaneder = 1),
                inntekt = Inntekt(
                    aarligBeloep = 25_000,
                    tomAlder = Alder(aar = 70, maaneder = 11)
                )
            ),
            utenlandsperiodeListe = listOf(
                UtenlandsperiodeSpec(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidetUtenlands = true
                )
            )
        )

        actual shouldBe expected
    }
}
