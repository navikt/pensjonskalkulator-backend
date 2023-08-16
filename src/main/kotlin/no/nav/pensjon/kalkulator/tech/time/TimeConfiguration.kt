package no.nav.pensjon.kalkulator.tech.time

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class TimeConfiguration {

    @Bean
    fun todayProvider() = DateProvider { LocalDate.now() }
}
