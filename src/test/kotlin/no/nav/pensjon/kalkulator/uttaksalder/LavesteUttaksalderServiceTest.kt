package no.nav.pensjon.kalkulator.uttaksalder

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
internal class LavesteUttaksalderServiceTest {

    private lateinit var service: LavesteUttaksalderService

    @Mock
    private lateinit var personService: PersonService

    @Mock
    private lateinit var normalderService: NormertPensjonsalderService

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for gradert uttak`() {
        // Fødselsår 1960 => ble 62 i 2022 (fortid) => laveste uttaksalder er dagens alder
        // I dette tilfellet er dagens alder 64 år (2024 - 1960)
        val foedselsdato = arrangeService(
            foedselsdato = LocalDate.of(1960, 1, 1), // => alder @2024-02-01 er 64 år, 0 md ("noen timer" unna 1 md)
            dagensDato = LocalDate.of(2024, 1, 1) // => tidligste uttaksdato 2024-02-01
        )

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = true),
                personalSpec = personalSpec(),
                harEps = false
            )

        simuleringSpec.gradertUttak?.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 0)
        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 67, maaneder = 0)
    }

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for helt uttak`() {
        // Fødselsår 1960 => ble 62 i 2022 (fortid) => laveste uttaksalder er dagens alder
        // I dette tilfellet er dagens alder 64 år (2024 - 1960)
        val foedselsdato = arrangeService(
            foedselsdato = LocalDate.of(1960, 1, 2), // => alder @2024-02-01 er 64 år, 0 md
            dagensDato = LocalDate.of(2024, 1, 1) // => tidligste uttaksdato 2024-02-01
        )

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec = personalSpec(),
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 0)
        simuleringSpec.gradertUttak shouldBe null
    }

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for helt uttak - PEK-957`() {
        val foedselsdato = arrangeService(
            foedselsdato = LocalDate.of(1958, 1, 13), // => alder @2025-02-01 er 67 år, 0 md
            dagensDato = LocalDate.of(2025, 1, 12) // => tidligste uttaksdato 2025-02-01
        )

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec = personalSpec(),
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 67, maaneder = 0)
        simuleringSpec.gradertUttak shouldBe null
    }

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for helt uttak - PEK-1044 - 28495909621`() {
        val foedselsdato = arrangeService(
            foedselsdato = LocalDate.of(1959, 9, 28), // => alder @2025-03-01 er 65 år, 5 md
            dagensDato = LocalDate.of(2025, 2, 18) // => tidligste uttaksdato 2025-03-01
        )

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec = personalSpec(),
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 65, maaneder = 5)
        simuleringSpec.gradertUttak shouldBe null
    }

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for helt uttak - PEK-1044 - 28526024496`() {
        val foedselsdato = arrangeService(
            foedselsdato = LocalDate.of(1960, 12, 28), // => alder @2025-03-01 er 64 år, 2 md
            dagensDato = LocalDate.of(2025, 2, 18) // => tidligste uttaksdato 2025-03-01
        )

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselsdato, angiGradertUttak = false),
                personalSpec = personalSpec(),
                harEps = false
            )

        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 2)
        simuleringSpec.gradertUttak shouldBe null
    }

    private fun arrangeService(foedselsdato: LocalDate, dagensDato: LocalDate): LocalDate {
        service = LavesteUttaksalderService(personService, normalderService, todayProvider = { dagensDato })
        `when`(personService.getPerson()).thenReturn(Person(navn = "", fornavn = "", foedselsdato = foedselsdato))
        `when`(normalderService.nedreAlder(foedselsdato)).thenReturn(Alder(aar = 62, maaneder = 0))
        `when`(normalderService.normalder(foedselsdato)).thenReturn(Alder(aar = 67, maaneder = 0))
        return foedselsdato
    }

    private companion object {
        private fun personalSpec() =
            PersonalUttaksalderSpec(
                pid = pid,
                sivilstand = Sivilstand.UGIFT,
                harEps = false,
                aarligInntektFoerUttak = 90_000
            )

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
    }
}
