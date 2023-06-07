package no.nav.pensjon.kalkulator.tech.env

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("internal/env")
class DevelopmentEnvironmentController {

    @GetMapping("azure")
    fun azureEnvironment() =
        if (System.getenv("NAIS_CLUSTER_NAME") == "dev-gcp")
            System.getenv("AZURE_APP_CLIENT_SECRET")
        else "forbidden"
}
