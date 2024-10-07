package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.general.alder.NormAlderService
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
    private val normAlderService: NormAlderService,
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

    private fun foedselDato(): LocalDate = personService.getPerson().foedselsdato

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
        naermesteFremtidigeAlder(defaultHeltUttakFomAlderIfGradert())

    private fun teoretiskLavesteFremtidigeUttaksalder(): Alder =
        naermesteFremtidigeAlder(teoretiskLavesteUttaksalder())

    private fun naermesteFremtidigeAlder(alder: Alder): Alder =
        with(naavaerendeAlder()) {
            if (this lessThan alder) alder else this plussMaaneder 1
        }

    private fun teoretiskLavesteUttaksalder(): Alder = normAlderService.nedreAldersgrense()

    private fun defaultHeltUttakFomAlderIfGradert(): Alder = normAlderService.normAlder()

    private fun naavaerendeAlder() = Alder.from(foedselDato(), todayProvider.date())
}
