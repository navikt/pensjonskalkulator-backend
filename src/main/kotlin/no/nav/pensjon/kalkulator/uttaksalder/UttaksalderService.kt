package no.nav.pensjon.kalkulator.uttaksalder

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import org.springframework.stereotype.Service

@Service
class UttaksalderService(
    private val uttaksalderClient: UttaksalderClient,
    private val inntektService: InntektService,
    private val personClient: PersonClient,
    private val pidGetter: PidGetter
) {
    private val log = KotlinLogging.logger {}

    fun finnTidligsteUttaksalder(impersonalSpec: ImpersonalUttaksalderSpec): Alder? {
        val pid = pidGetter.pid()
        val sivilstand = impersonalSpec.sivilstand ?: sivilstand(pid)

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = sivilstand,
            harEps = impersonalSpec.harEps ?: sivilstand.harEps,
            aarligInntektFoerUttak = impersonalSpec.aarligInntektFoerUttak ?: sisteInntekt()
        )

        log.debug { "Finner første mulige uttaksalder med parametre $impersonalSpec og $personalSpec" }
        return uttaksalderClient.finnTidligsteUttaksalder(impersonalSpec, personalSpec).also(::updateMetric)
    }

    private fun sisteInntekt() = inntektService.sistePensjonsgivendeInntekt().beloep.intValueExact()

    private fun sivilstand(pid: Pid): Sivilstand =
        personClient.fetchPerson(pid)?.sivilstand ?: Sivilstand.UOPPGITT

    private companion object {
        private val teoretiskLavesteUttaksalder = Alder(62, 0)

        private fun updateMetric(alder: Alder?) {
            val maaneder = alder?.let {
                if (teoretiskLavesteUttaksalder == it) it.maaneder.toString() else "x"
            }

            val result = maaneder?.let { "${alder.aar}/$maaneder" } ?: "null"
            Metrics.countEvent("uttaksalder", result)
        }
    }
}
