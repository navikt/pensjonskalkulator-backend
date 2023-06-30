package no.nav.pensjon.kalkulator.tech.trace

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class TraceConfiguration {

    @Bean
    fun callIdGenerator() = CallIdGenerator { UUID.randomUUID().toString() }
}
