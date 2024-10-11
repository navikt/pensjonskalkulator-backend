package no.nav.pensjon.kalkulator.tech.time

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class TodayProvider : DateProvider {
    override fun date(): LocalDate = LocalDate.now()
}
