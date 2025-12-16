package no.nav.pensjon.kalkulator

import io.prometheus.client.hotspot.DefaultExports
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class PensjonskalkulatorBackendApplication

fun main(args: Array<String>) {
    DefaultExports.initialize()
    runApplication<PensjonskalkulatorBackendApplication>(*args)
}
