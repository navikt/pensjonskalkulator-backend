package no.nav.pensjon.kalkulator.tech.toggle.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.tech.time.Timed
import no.nav.pensjon.kalkulator.tech.toggle.FeatureToggleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api")
class FeatureToggleController(val service: FeatureToggleService) : Timed() {

    @GetMapping("feature/{name}")
    @Operation(
        summary = "Hvorvidt en gitt funksjonsbryter er skrudd på",
        description = "Hent status for en gitt funksjonsbryter (hvorvidt funksjonen er skrudd på)"
    )
    fun isEnabled(@PathVariable(value = "name") featureName: String) =
        EnablementDto(timed(service::isEnabled, featureName, "feature"))
}
