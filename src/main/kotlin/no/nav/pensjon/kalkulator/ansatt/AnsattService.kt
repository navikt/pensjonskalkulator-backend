package no.nav.pensjon.kalkulator.ansatt

import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.springframework.stereotype.Service

@Service
class AnsattService(private val ansattIdExtractor: SecurityContextNavIdExtractor) {

    fun getAnsattId(): String = ansattIdExtractor.id()
}
