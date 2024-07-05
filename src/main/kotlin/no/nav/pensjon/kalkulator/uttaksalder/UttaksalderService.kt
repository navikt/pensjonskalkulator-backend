package no.nav.pensjon.kalkulator.uttaksalder

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class UttaksalderService(
    private val simuleringService: SimuleringService,
    private val inntektService: InntektService,
    private val personService: PersonService,
    private val pidGetter: PidGetter
) {
    private val log = KotlinLogging.logger {}

    /**
     * Tidligst mulig uttak (TMU) er kun mulig å finne ved helt uttak, da det i dette tilfellet bare er én
     * uttaksdato å beregne.
     * (Ved gradert uttak er det to uttaksdatoer som gjensidig påvirker hverandre.)
     */
    fun finnTidligsteUttaksalder(impersonalSpec: ImpersonalUttaksalderSpec): Alder? {
        validate(impersonalSpec)
        val pid = pidGetter.pid()
        val sivilstand = impersonalSpec.sivilstand ?: sivilstand()
        val harEps = impersonalSpec.harEps ?: sivilstand.harEps

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = sivilstand,
            harEps = harEps,
            aarligInntektFoerUttak = impersonalSpec.aarligInntektFoerUttak ?: sisteInntekt()
        )

        val result = simuleringService.simulerAlderspensjon(forLavesteUttaksalder(impersonalSpec, personalSpec, harEps))
        val tmuAlder = result.vilkaarsproeving.alternativ?.heltUttakAlder ?: teoretiskLavesteUttaksalder
        return tmuAlder.also(::updateMetric)
    }

    private fun validate(impersonalSpec: ImpersonalUttaksalderSpec) {
        if (impersonalSpec.gradertUttak != null) {
            "kan ikke finne TMU for gradert uttak".let {
                log.warn { it }
                throw IllegalArgumentException(it)
            }
        }
    }

    private fun forLavesteUttaksalder(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec,
        harEps: Boolean
    ) =
        ImpersonalSimuleringSpec(
            simuleringType = impersonalSpec.simuleringType,
            sivilstand = personalSpec.sivilstand,
            epsHarInntektOver2G = harEps, // antagelse: de fleste ektefeller/partnere/samboere har inntekt over 2G
            forventetAarligInntektFoerUttak = personalSpec.aarligInntektFoerUttak,
            gradertUttak = impersonalSpec.gradertUttak?.let(::simuleringGradertUttak),
            heltUttak = simuleringHeltUttak(impersonalSpec),
            utenlandsperiodeListe = impersonalSpec.utenlandsperiodeListe
        )


    private fun sisteInntekt() = inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()

    private fun sivilstand(): Sivilstand = personService.getPerson().sivilstand

    private companion object {
        private val teoretiskLavesteUttaksalder = Alder(aar = 62, maaneder = 0)
        private val defaultHeltUttakFomAlderIfGradert = Alder(aar = 67, maaneder = 0)

        private fun simuleringGradertUttak(source: UttaksalderGradertUttak) =
            GradertUttak(
                grad = source.grad,
                uttakFomAlder = teoretiskLavesteUttaksalder,
                aarligInntekt = source.aarligInntekt
            )

        private fun simuleringHeltUttak(spec: ImpersonalUttaksalderSpec) =
            HeltUttak(
                uttakFomAlder = spec.gradertUttak
                    ?.let { defaultHeltUttakFomAlderIfGradert }
                    ?: teoretiskLavesteUttaksalder,
                inntekt = spec.heltUttak?.inntekt
            )

        private fun updateMetric(alder: Alder?) {
            val maaneder = alder?.let {
                if (teoretiskLavesteUttaksalder == it) it.maaneder.toString() else "x"
            }

            val result = maaneder?.let { "${alder.aar}/$maaneder" } ?: "null"
            Metrics.countEvent(eventName = "uttaksalder", result = result)
        }
    }
}
