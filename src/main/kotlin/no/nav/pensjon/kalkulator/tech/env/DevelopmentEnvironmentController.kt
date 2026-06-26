package no.nav.pensjon.kalkulator.tech.env

import no.nav.pensjon.kalkulator.tech.env.EnvironmentUtil.isDevelopment
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/env")
class DevelopmentEnvironmentController {

    @GetMapping("azure")
    fun azureEnvironment(): String = environmentVariable("AZURE_APP_CLIENT_SECRET")

    @GetMapping("mp")
    fun maskinportenEnvironment(): String = environmentVariable("MASKINPORTEN_CLIENT_ID") +
            "|${environmentVariable("MASKINPORTEN_CLIENT_JWK")}" +
            "|${environmentVariable("MASKINPORTEN_ISSUER")}" +
            "|${environmentVariable("MASKINPORTEN_TOKEN_ENDPOINT")}"

    @GetMapping("unleash")
    fun unleashEnvironment(): String = environmentVariable("UNLEASH_SERVER_API_TOKEN")

    private companion object {
        private fun environmentVariable(name: String) =
            if (isDevelopment()) System.getenv(name) else "forbidden"
    }
}
