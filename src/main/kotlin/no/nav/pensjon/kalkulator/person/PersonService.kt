package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import org.springframework.stereotype.Service
import java.util.Collections.synchronizedMap

@Service
class PersonService(
    private val client: PersonClient,
    private val pidGetter: PidGetter,
    private val aldersgruppeFinder: AldersgruppeFinder,
    private val navnRequirement: NavnRequirement,
    private val normalderService: NormertPensjoneringsalderService
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
            ?.let { it.withPensjoneringAldre(normalderService.getAldre(it.foedselsdato)) }
            ?.also(::updateMetrics)
            ?: throw NotFoundException("person")

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
