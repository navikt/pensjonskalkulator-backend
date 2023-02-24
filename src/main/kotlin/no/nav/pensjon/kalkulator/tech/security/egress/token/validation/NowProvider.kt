package no.nav.pensjon.kalkulator.tech.security.egress.token.validation

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NowProvider : TimeProvider {
    override fun time(): LocalDateTime {
        return LocalDateTime.now()
    }
}
