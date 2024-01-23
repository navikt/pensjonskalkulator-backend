package no.nav.pensjon.kalkulator.uttaksalder

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.UttaksalderGradertUttak
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import org.springframework.stereotype.Service

@Service
class UttaksalderService(
    private val uttaksalderClient: UttaksalderClient,
    private val simuleringService: SimuleringService,
    private val inntektService: InntektService,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    private val log = KotlinLogging.logger {}

    /**
     * Algoritme for å finne tidligste uttaksalder:
     * (1) Prøv simulering med teoretisk laveste uttaksalder (pr. 1.1.2024 er det 62 år)
     * (2) (a) Hvis (1) er OK, så er tidligste uttaksalder 62 år
     *     (b) Hvis (1) feiler, finn tidligste uttaksalder gjennom PENs 'prøve seg fram'-tjeneste
     *         (som gjør gjentatte simuleringsforsøk inntil tidligste uttaksalder er funnet)
     *  Denne algoritmen gjør at man ofte finner tidligste uttaksalder med én simulering (da mange har
     *  nok opptjening ved 62 år), mens PENs 'prøve seg fram'-tjeneste alltid bruker 6 simuleringer.
     */
    fun finnTidligsteUttaksalder(impersonalSpec: ImpersonalUttaksalderSpec): Alder? {
        val pid = pidGetter.pid()
        val sivilstand = impersonalSpec.sivilstand ?: sivilstand(pid)
        val harEps = impersonalSpec.harEps ?: sivilstand.harEps

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = sivilstand,
            harEps = harEps,
            aarligInntektFoerUttak = impersonalSpec.aarligInntektFoerUttak ?: sisteInntekt()
        )

        return try {
            simuleringService.simulerAlderspensjon(specMedLavesteUttaksalder(impersonalSpec, personalSpec, harEps))
            teoretiskLavesteUttaksalder
        } catch (e: EgressException) {
            // Vil havne her hvis bruker har for lav opptjening for uttak ved teoretisk laveste alder
            useTrialAndError(impersonalSpec, personalSpec)
        }
    }

    private fun specMedLavesteUttaksalder(
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
            heltUttak = simuleringHeltUttak(impersonalSpec)
        )

    private fun useTrialAndError(
        impersonalSpec: ImpersonalUttaksalderSpec,
        personalSpec: PersonalUttaksalderSpec
    ): Alder? {
        log.debug { "Finner første mulige uttaksalder med parametre $impersonalSpec og $personalSpec" }
        return uttaksalderClient.finnTidligsteUttaksalder(impersonalSpec, personalSpec).also(::updateMetric)
    }

    private fun sisteInntekt() = inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()

    private fun sivilstand(pid: Pid): Sivilstand =
        personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT

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
