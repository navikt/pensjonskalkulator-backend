package no.nav.pensjon.kalkulator.uttaksalder

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringException
import no.nav.pensjon.kalkulator.simulering.SimuleringService
import no.nav.pensjon.kalkulator.simulering.SimuleringStatus
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class UttaksalderService(
    private val simuleringService: SimuleringService,
    private val inntektService: InntektService,
    private val personService: PersonService,
    private val pidGetter: PidGetter,
    private val normalderService: NormertPensjonsalderService,
    private val lavesteUttaksalderService: LavesteUttaksalderService
) {
    private val log = KotlinLogging.logger {}

    /**
     * Tidligst mulig uttak (TMU) er kun mulig å finne ved helt uttak, da det i dette tilfellet bare er én
     * uttaksdato å beregne.
     * (Ved gradert uttak er det to uttaksdatoer som gjensidig påvirker hverandre.)
     */
    fun finnTidligsteUttaksalder(impersonalSpec: ImpersonalUttaksalderSpec): Alder? {
        validate(impersonalSpec)
        val sivilstand = impersonalSpec.sivilstand ?: sivilstand()
        val harEps = impersonalSpec.harEps ?: sivilstand.harEps

        val personalSpec = PersonalUttaksalderSpec(
            pid = pidGetter.pid(),
            sivilstand, harEps,
            aarligInntektFoerUttak = impersonalSpec.aarligInntektFoerUttak ?: sisteInntekt()
        )

        val gunstigstSimuleringSpec =
            lavesteUttaksalderService.lavesteUttaksalderSimuleringSpec(impersonalSpec, personalSpec, harEps)

        val result = simuleringService.simulerPersonligAlderspensjon(gunstigstSimuleringSpec)

        if (
            impersonalSpec.simuleringType == SimuleringType.ALDERSPENSJON_MED_AFP_OFFENTLIG_LIVSVARIG &&
            result.afpOffentlig.isEmpty()
        )
            throw SimuleringException(
                status = SimuleringStatus.AFP_IKKE_I_VILKAARSPROEVING
            )


        // TMU er enten:
        // - Den lavest mulige fremtidige alder for helt uttak (hvis vilkårsprøvingen av denne gir OK), eller
        // - Den alternative alder for helt uttak som returneres av simuleringen (vilkårsprøvingen av denne har gitt OK)
        val tmuAlder = result.vilkaarsproeving.alternativ?.heltUttakAlder
            ?: gunstigstSimuleringSpec.heltUttak.uttakFomAlder

        return tmuAlder.also(::updateMetric)
    }

    private fun validate(spec: ImpersonalUttaksalderSpec) {
        if (spec.gradertUttak != null) {
            "kan ikke finne TMU for gradert uttak".let {
                log.warn { it }
                throw IllegalArgumentException(it)
            }
        }
    }

    private fun sisteInntekt(): Int =
        inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()

    private fun sivilstand(): Sivilstand =
        personService.getPerson().sivilstand

    private fun teoretiskLavesteUttaksalder(): Alder =
        normalderService.nedreAlder(personService.getPerson().foedselsdato)

    private fun updateMetric(alder: Alder?) {
        val maaneder = alder?.let {
            if (teoretiskLavesteUttaksalder() == it) it.maaneder.toString() else "x"
        }

        val result = maaneder?.let { "${alder.aar}/$maaneder" } ?: "null"
        Metrics.countEvent(eventName = "uttaksalder", result = result)
    }
}
