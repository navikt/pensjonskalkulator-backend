package no.nav.pensjon.kalkulator.tech.security.ingress.csrf

import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Obtains a CSRF token for protection against cross-site request forgery.
 */
@RestController
class CsrfController {

    @GetMapping("/api/csrf")
    fun csrf(csrfToken: CsrfToken): CsrfToken = csrfToken
}
