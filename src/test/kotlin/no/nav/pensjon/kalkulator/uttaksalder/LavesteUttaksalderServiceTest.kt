package no.nav.pensjon.kalkulator.uttaksalder

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.general.alder.NormAlderService
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
internal class LavesteUttaksalderServiceTest {

    private lateinit var service: LavesteUttaksalderService

    @Mock
    private lateinit var personService: PersonService

    @Mock
    private lateinit var normAlderService: NormAlderService

    @BeforeEach
    fun initialize() {
        service = LavesteUttaksalderService(
            personService, normAlderService,
            todayProvider = { LocalDate.of(2024, 1, 1) }
        )

        `when`(normAlderService.nedreAldersgrense()).thenReturn(Alder(aar = 62, maaneder = 0))
        `when`(normAlderService.normAlder()).thenReturn(Alder(aar = 67, maaneder = 0))
    }

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for gradert uttak`() {
        // Fødselsår 1960 => ble 62 i 2022 (fortid) => laveste uttaksalder er dagens alder
        // I dette tilfellet er dagens alder 64 år (2024 - 1960)
        val foedselDato = LocalDate.of(1960, 1, 1)
        `when`(personService.getPerson()).thenReturn(Person(navn = "", foedselDato))

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselDato, angiGradertUttak = true),
                personalSpec = personalSpec(),
                harEps = false
            )

        // Uttak kan starte måneden etter fylte 64 år:
        simuleringSpec.gradertUttak?.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 1)
        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 67, maaneder = 0)
    }

    @Test
    fun `lavesteUttaksalderSimuleringSpec inneholder laveste fremtidige alder for helt uttak`() {
        // Fødselsår 1960 => ble 62 i 2022 (fortid) => laveste uttaksalder er dagens alder
        // I dette tilfellet er dagens alder 64 år (2024 - 1960)
        val foedselDato = LocalDate.of(1960, 1, 1)
        `when`(personService.getPerson()).thenReturn(Person(navn = "", foedselDato))

        val simuleringSpec =
            service.lavesteUttaksalderSimuleringSpec(
                impersonalSpec = impersonalSpec(foedselDato, angiGradertUttak = false),
                personalSpec = personalSpec(),
                harEps = false
            )

        // Uttak kan starte måneden etter fylte 64 år:
        simuleringSpec.heltUttak.uttakFomAlder shouldBe Alder(aar = 64, maaneder = 1)
        simuleringSpec.gradertUttak shouldBe null
    }

    private companion object {
        private fun personalSpec() =
            PersonalUttaksalderSpec(
                pid = pid,
                sivilstand = Sivilstand.UGIFT,
                harEps = false,
                aarligInntektFoerUttak = 90_000
            )

        private fun impersonalSpec(foedselDato: LocalDate, angiGradertUttak: Boolean) =
            ImpersonalUttaksalderSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                sivilstand = Sivilstand.GIFT,
                harEps = true,
                aarligInntektFoerUttak = 100_000,
                gradertUttak = if (angiGradertUttak)
                    UttaksalderGradertUttak(
                        grad = Uttaksgrad.FEMTI_PROSENT,
                        aarligInntekt = 50_000,
                        foedselDato
                    ) else null,
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(aar = 67, maaneder = 0),
                    inntekt = null
                ),
                utenlandsperiodeListe = emptyList()
            )
    }
}
