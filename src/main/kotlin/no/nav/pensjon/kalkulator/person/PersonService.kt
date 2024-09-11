package no.nav.pensjon.kalkulator.person

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.tech.metric.Metrics
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PersonService(
    private val client: PersonClient,
    private val pidGetter: PidGetter,
    private val aldersgruppeFinder: AldersgruppeFinder,
    private val navnRequirement: NavnRequirement
) {
    private val log = KotlinLogging.logger {}

    fun getPerson(): Person =
        client.fetchPerson(
            pid = pidGetter.pid().also(::validate),
            fetchFulltNavn = navnRequirement.needFulltNavn()
        ).also(::observe) ?: throw NotFoundException("person")

    private fun validate(pid: Pid) {
        if (pid.isValid.not()) throw NotFoundException("person")
    }

    private fun observe(person: Person?) {
        person?.let {
            checkAlder(it.foedselsdato)
            updateMetrics(it)
        }
    }

    private fun checkAlder(foedselsdato: LocalDate) {
        foedselsdato.let {
            if (it < TIDLIGSTE_STOETTEDE_FOEDSELSDATO) {
                log.warn { "For tidlig fødselsdato - $it" }
            }
        }
    }

    private fun updateMetrics(person: Person) {
        Metrics.countEvent(ALDERSGRUPPE_METRIC_NAME, aldersgruppeFinder.aldersgruppe(person))
    }

    private companion object {
        private const val ALDERSGRUPPE_METRIC_NAME = "aldersgruppe"
        private val TIDLIGSTE_STOETTEDE_FOEDSELSDATO = LocalDate.of(1963, 1, 1)
    }
}
