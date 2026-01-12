package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorAlderSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorGradertUttakSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorHeltUttakSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.SimulatorUtlandPeriodeSpec
import java.time.LocalDate

class SimulatorPersonligSimuleringSpecMapperTest : ShouldSpec({

    should("map domain object to simulator-specific data transfer object") {
        SimulatorPersonligSimuleringSpecMapper.toDto(impersonalSpec(), personalSpec()) shouldBe
                SimulatorSimuleringSpec(
                    simuleringstype = "ALDER",
                    pid = "12906498357",
                    sivilstand = "UGIF",
                    epsHarPensjon = false,
                    epsHarInntektOver2G = true,
                    sisteInntekt = 100_000,
                    uttaksar = 1,
                    gradertUttak = SimulatorGradertUttakSpec(
                        grad = "P_80",
                        uttakFomAlder = SimulatorAlderSpec(aar = 67, maaneder = 1),
                        aarligInntekt = 12_000
                    ),
                    heltUttak = SimulatorHeltUttakSpec(
                        uttakFomAlder = SimulatorAlderSpec(aar = 68, maaneder = 11),
                        aarligInntekt = 6_000,
                        inntektTomAlder = SimulatorAlderSpec(aar = 70, maaneder = 3)
                    ),
                    utenlandsperiodeListe = listOf(
                        SimulatorUtlandPeriodeSpec(
                            fom = LocalDate.of(1990, 1, 2),
                            tom = LocalDate.of(1999, 11, 30),
                            land = "AUS",
                            arbeidetUtenlands = true
                        )
                    ),
                    afpInntektMaanedFoerUttak = null,
                    afpOrdning = "AFPKOM",
                    innvilgetLivsvarigOffentligAfp = null
                )
    }
})

private fun impersonalSpec() =
    ImpersonalSimuleringSpec(
        simuleringType = SimuleringType.ALDERSPENSJON,
        eps = Eps(harInntektOver2G = true, harPensjon = false),
        gradertUttak = GradertUttak(
            grad = Uttaksgrad.AATTI_PROSENT,
            uttakFomAlder = Alder(aar = 67, maaneder = 1),
            aarligInntekt = 12_000
        ),
        heltUttak = HeltUttak(
            uttakFomAlder = Alder(68, 11),
            inntekt = Inntekt(
                aarligBeloep = 6_000,
                tomAlder = Alder(aar = 70, maaneder = 3)
            )
        ),
        utenlandsopphold = Utenlandsopphold(
            periodeListe = listOf(
                Opphold(
                    fom = LocalDate.of(1990, 1, 2),
                    tom = LocalDate.of(1999, 11, 30),
                    land = Land.AUS,
                    arbeidet = true
                )
            )
        )
    )

private fun personalSpec() =
    PersonalSimuleringSpec(
        pid = pid,
        aarligInntektFoerUttak = 100_000,
        sivilstand = Sivilstand.UGIFT
    )
