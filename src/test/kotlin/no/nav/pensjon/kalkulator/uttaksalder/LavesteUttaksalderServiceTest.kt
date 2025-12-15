package no.nav.pensjon.kalkulator.uttaksalder

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import java.time.LocalDate

class LavesteUttaksalderServiceTest : ShouldSpec({

    val personalSpec = PersonalUttaksalderSpec(
        pid = pid,
        sivilstand = Sivilstand.UGIFT,
        harEps = false,
        aarligInntektFoerUttak = 90_000
    )

    should("returnere laveste fremtidige alder for gradert uttak") {
        // Fødselsår 1960 => ble 62 i 2022 (fortid) => laveste uttaksalder er dagens alder
        // I dette tilfellet er dagens alder 64 år (2024 - 1960)
        val foedselsdato = LocalDate.of(1960, 1, 1) // => alder @2024-02-01 er 64 år, 0 md ("noen timer" unna 1 md)
        val dagensDato = LocalDate.of(2024, 1, 1) // => tidligste uttaksdato 2024-02-01
        val service = arrangeService(foedselsdato, dagensDato)

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = true),
                personalSpec,
                harEps = false
            )

        simuleringSpec.gradertUttak?.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 0)
        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 67, maaneder = 0)
    }

    should("returnere laveste fremtidige alder for helt uttak") {
        // Fødselsår 1960 => ble 62 i 2022 (fortid) => laveste uttaksalder er dagens alder
        // I dette tilfellet er dagens alder 64 år (2024 - 1960)
        val foedselsdato = LocalDate.of(1960, 1, 2) // => alder @2024-02-01 er 64 år, 0 md
        val dagensDato = LocalDate.of(2024, 1, 1) // => tidligste uttaksdato 2024-02-01

        val service = arrangeService(foedselsdato, dagensDato)

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec,
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 0)
        simuleringSpec.gradertUttak shouldBe null
    }

    should("returnere laveste fremtidige alder for helt uttak - PEK-957") {
        val foedselsdato = LocalDate.of(1958, 1, 13) // => alder @2025-02-01 er 67 år, 0 md
        val dagensDato = LocalDate.of(2025, 1, 12) // => tidligste uttaksdato 2025-02-01

        val service = arrangeService(foedselsdato, dagensDato)

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec,
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 67, maaneder = 0)
        simuleringSpec.gradertUttak shouldBe null
    }

    should("returnere laveste fremtidige alder for helt uttak - PEK-1044 - 28495909621") {
        val foedselsdato = LocalDate.of(1959, 9, 28) // => alder @2025-03-01 er 65 år, 5 md
        val dagensDato = LocalDate.of(2025, 2, 18) // => tidligste uttaksdato 2025-03-01
        val service = arrangeService(foedselsdato, dagensDato)

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec,
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 65, maaneder = 5)
        simuleringSpec.gradertUttak shouldBe null
    }

    should("returnere laveste fremtidige alder for helt uttak - PEK-1044 - 28526024496") {
        val foedselsdato = LocalDate.of(1960, 12, 28) // => alder @2025-03-01 er 64 år, 2 md
        val dagensDato = LocalDate.of(2025, 2, 18) // => tidligste uttaksdato 2025-03-01
        val service = arrangeService(foedselsdato, dagensDato)

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec,
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 2)
        simuleringSpec.gradertUttak shouldBe null
    }
})

private fun arrangeService(
    foedselsdato: LocalDate,
    dagensDato: LocalDate
): LavesteUttaksalderService {
    val service = LavesteUttaksalderService(
        personService = arrangePerson(foedselsdato),
        normalderService = arrangeNormalder(),
        todayProvider = { dagensDato })
    return service
}

private fun arrangeNormalder(): NormertPensjonsalderService =
    mockk<NormertPensjonsalderService>().apply {
        every { nedreAlder(any()) } returns Alder(aar = 62, maaneder = 0)
        every { normalder(any()) } returns Alder(aar = 67, maaneder = 0)
    }

private fun arrangePerson(foedselsdato: LocalDate): PersonService =
    mockk<PersonService>().apply {
        every { getPerson() } returns Person(navn = "", fornavn = "", foedselsdato = foedselsdato)
    }

private fun impersonalSpec(foedselsdato: LocalDate, angiGradertUttak: Boolean) =
    ImpersonalUttaksalderSpec(
        simuleringType = SimuleringType.ALDERSPENSJON,
        sivilstand = Sivilstand.GIFT,
        harEps = true,
        aarligInntektFoerUttak = 100_000,
        gradertUttak = if (angiGradertUttak)
            UttaksalderGradertUttak(
                grad = Uttaksgrad.FEMTI_PROSENT,
                aarligInntekt = 50_000,
                foedselsdato
            ) else null,
        heltUttak = HeltUttak(
            uttakFomAlder = Alder(aar = 67, maaneder = 0),
            inntekt = null
        ),
        utenlandsperiodeListe = emptyList()
    )
