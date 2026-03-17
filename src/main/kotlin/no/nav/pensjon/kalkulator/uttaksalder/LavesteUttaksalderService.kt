package no.nav.pensjon.kalkulator.uttaksalder

/*
no answer found for LavesteUttaksalderService(#10).lavesteUttaksalderSimuleringSpec(
ImpersonalUttaksalderSpec(simuleringType=ALDERSPENSJON, sivilstatus=null, harEps=false, aarligInntektFoerUttak=null, gradertUttak=null, heltUttak=HeltUttak(uttakFomAlder=Alder(aar=67, maaneder=0), inntekt=null), utenlandsperiodeListe=[Opphold(fom=1990-01-02, tom=1999-11-30, land=AUS, arbeidet=true)], innvilgetLivsvarigOffentligAfp=null), PersonalUttaksalderSpec(pid=Pid(child of #7#12), sivilstatus=GIFT, harEps=true, aarligInntektFoerUttak=543210), true)
ImpersonalUttaksalderSpec(simuleringType=ALDERSPENSJON, sivilstatus=null, harEps=false, aarligInntektFoerUttak=null, gradertUttak=null, heltUttak=HeltUttak(uttakFomAlder=Alder(aar=67, maaneder=0), inntekt=null), utenlandsperiodeListe=[Opphold(fom=1990-01-02, tom=1999-11-30, land=AUS, arbeidet=true)], innvilgetLivsvarigOffentligAfp=null)), any<PersonalUttaksalderSpec>(), eq(false))))

 */
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.simulering.EpsSpec
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.LevendeEps
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
            sivilstatus = personalSpec.sivilstatus,
            eps = EpsSpec(
                LevendeEps(
                    harInntektOver2G = harEps, // antagelse: de fleste ektefeller/partnere/samboere har inntekt over 2G
                    harPensjon = false
                )
            ),
            forventetAarligInntektFoerUttak = personalSpec.aarligInntektFoerUttak,
            gradertUttak = impersonalSpec.gradertUttak?.let(::simuleringGradertUttak),
            heltUttak = simuleringHeltUttak(impersonalSpec),
            utenlandsopphold = Utenlandsopphold(
                periodeListe = impersonalSpec.utenlandsperiodeListe,
                antallAar = null
            ),
            innvilgetLivsvarigOffentligAfp = impersonalSpec.innvilgetLivsvarigOffentligAfp
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
