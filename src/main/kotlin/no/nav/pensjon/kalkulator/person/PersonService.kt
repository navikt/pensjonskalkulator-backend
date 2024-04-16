package no.nav.pensjon.kalkulator.person

import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class PersonService(
    private val client: PersonClient,
    private val pidGetter: PidGetter,
    private val aldersgruppeFinder: AldersgruppeFinder,
    private val navnRequirement: NavnRequirement
) {

    fun getPerson() = client.fetchPerson(
        pid = pidGetter.pid(),
        fetchFulltNavn = navnRequirement.needFulltNavn()
    ).also(::updateMetrics)

    private fun updateMetrics(person: Person?) {
        person?.let { Metrics.countEvent(ALDERSGRUPPE_METRIC_NAME, aldersgruppeFinder.aldersgruppe(it)) }
    }

    private companion object {
        private const val ALDERSGRUPPE_METRIC_NAME = "aldersgruppe"
    }
}
