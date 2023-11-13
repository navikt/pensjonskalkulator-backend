package no.nav.pensjon.kalkulator.tech.security.ingress

import mu.KotlinLogging
import org.apache.catalina.connector.Request
import org.apache.catalina.connector.Response
import org.apache.catalina.valves.ValveBase
import org.springframework.http.HttpHeaders

class RequestContextValve : ValveBase() {

    private val log = KotlinLogging.logger {}

    override fun invoke(request: Request, response: Response) {
        val auth = request.getHeader(HttpHeaders.AUTHORIZATION)
        log.info { ">>>>>>>>> AUTH: $auth" }
        response.setHeader("Nav-Call-Id", auth)
        getNext().invoke(request, response)
    }
}
