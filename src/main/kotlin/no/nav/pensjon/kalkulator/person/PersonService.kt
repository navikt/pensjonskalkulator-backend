package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service
import java.util.Collections.synchronizedMap

@Service
class PersonService(
    private val client: PersonClient,
    private val pidGetter: PidGetter,
    private val aldersgruppeFinder: AldersgruppeFinder,
    private val navnRequirement: NavnRequirement,
    private val normalderService: NormertPensjonsalderService
) {
    private val cachedPersonerVedPid: MutableMap<Pid, Person> = synchronizedMap(mutableMapOf())

    fun getPerson(): Person =
        with(pidGetter.pid()) {
            cachedPersonerVedPid[this] ?: fetchPerson(pid = this).also {
                limitCacheSize()
                cachedPersonerVedPid[this] = it
            }
        }

    private fun fetchPerson(pid: Pid): Person =
        client.fetchPerson(
            pid = pid.also(::validate),
            fetchFulltNavn = navnRequirement.needFulltNavn()
        )
            //?.let { it.withPensjoneringAldre(normalderService.getAldre(it.foedselsdato)) }
            ?.let { it.withPensjoneringAldre(aldersgrenser(it, pid)) }
            ?.also(::updateMetrics)
            ?: throw NotFoundException("person")

    /**
     * Temporary function to test 'økte aldersgrenser'.
     */
    private fun aldersgrenser(person: Person, pid: Pid): Aldersgrenser =
        when (pid.value) {
            "11466909302" -> Aldersgrenser(
                aarskull = 1969,
                nedreAlder = Alder(62, 4),
                normalder = Alder(67, 4),
                oevreAlder = Alder(75, 4),
                verdiStatus = VerdiStatus.PROGNOSE
            )
            "21437409932" -> Aldersgrenser(
                aarskull = 1974,
                nedreAlder = Alder(63, 0),
                normalder = Alder(68, 0),
                oevreAlder = Alder(76, 0),
                verdiStatus = VerdiStatus.PROGNOSE
            )
            else -> normalderService.aldersgrenser(person.foedselsdato)
        }

    private fun validate(pid: Pid) {
        if (pid.isValid.not()) throw NotFoundException("person")
    }

    private fun updateMetrics(person: Person) {
        Metrics.countEvent(ALDERSGRUPPE_METRIC_NAME, aldersgruppeFinder.aldersgruppe(person))
    }

    private fun limitCacheSize() {
        if (cachedPersonerVedPid.size > MAX_PERSON_CACHE_SIZE) {
            cachedPersonerVedPid.clear()
        }
    }

    private companion object {
        private const val MAX_PERSON_CACHE_SIZE = 1000
        private const val ALDERSGRUPPE_METRIC_NAME = "aldersgruppe"
    }
}
