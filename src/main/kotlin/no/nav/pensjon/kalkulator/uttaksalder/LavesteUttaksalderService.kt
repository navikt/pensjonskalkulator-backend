package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.simulering.Eps
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.Utenlandsopphold
import no.nav.pensjon.kalkulator.tech.time.DateProvider
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LavesteUttaksalderService(
    private val personService: PersonService,
    private val normalderService: NormertPensjonsalderService,
    private val todayProvider: DateProvider
) {
    fun lavesteUttaksalderSimuleringSpec(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec,
        harEps: Boolean
    ) =
        ImpersonalSimuleringSpec(
            simuleringType = impersonalSpec.simuleringType,
            sivilstand = personalSpec.sivilstand,
            eps = Eps(
                harInntektOver2G = harEps, // antagelse: de fleste ektefeller/partnere/samboere har inntekt over 2G
                harPensjon = false
            ),
            forventetAarligInntektFoerUttak = personalSpec.aarligInntektFoerUttak,
            gradertUttak = impersonalSpec.gradertUttak?.let(::simuleringGradertUttak),
            heltUttak = simuleringHeltUttak(impersonalSpec),
            utenlandsopphold = Utenlandsopphold(
                periodeListe = impersonalSpec.utenlandsperiodeListe,
                antallAar = null
            )
        )

    private fun foedselsdato(): LocalDate =
        personService.getPerson().foedselsdato

    private fun simuleringGradertUttak(source: UttaksalderGradertUttak) =
        GradertUttak(
            grad = source.grad,
            uttakFomAlder = teoretiskLavesteFremtidigeUttaksalder(),
            aarligInntekt = source.aarligInntekt
        )

    private fun simuleringHeltUttak(spec: ImpersonalUttaksalderSpec) =
        HeltUttak(
            uttakFomAlder = spec.gradertUttak
                ?.let { defaultHeltUttakFremtidigFomAlderIfGradert() }
                ?: teoretiskLavesteFremtidigeUttaksalder(),
            inntekt = spec.heltUttak?.inntekt
        )

    private fun defaultHeltUttakFremtidigFomAlderIfGradert(): Alder =
        alderPaaFremtidigUttaksdato(defaultHeltUttakFomAlderIfGradert())

    private fun teoretiskLavesteFremtidigeUttaksalder(): Alder =
        alderPaaFremtidigUttaksdato(teoretiskLavesteUttaksalder())

    /**
     * 'Nærmeste fremtidige alder' er alder på 1. dag av neste måned.
     */
    private fun alderPaaFremtidigUttaksdato(minimumAlder: Alder): Alder =
        with(alderPaaNaermesteFremtidigeUttaksdato()) {
            if (this lessThan minimumAlder) minimumAlder else this
        }

    private fun teoretiskLavesteUttaksalder(): Alder =
        normalderService.nedreAlder(personService.getPerson().foedselsdato)

    private fun defaultHeltUttakFomAlderIfGradert(): Alder =
        normalderService.normalder(personService.getPerson().foedselsdato)

    private fun alderPaaNaermesteFremtidigeUttaksdato() =
        Alder.from(
            foedselDato = foedselsdato(),
            dato = todayProvider.date().plusMonths(1).withDayOfMonth(1)
        )
}
