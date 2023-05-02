package no.nav.pensjon.kalkulator.simulering.api

import io.swagger.v3.oas.annotations.Operation
import no.nav.pensjon.kalkulator.simulering.Simuleringsresultat
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto
import no.nav.pensjon.kalkulator.simulering.api.map.SimuleringMapper.asSpec
import no.nav.pensjon.kalkulator.simulering.client.SimuleringClient
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class SimuleringController(private val simuleringClient: SimuleringClient) {

    @PostMapping("alderspensjon/simulering")
    @Operation(
        summary = "Simuler alderspensjon",
        description = "Lag en prognose for framtidig alderspensjon"
    )
    fun simulerAlderspensjon(@RequestBody specDto: SimuleringSpecDto): Simuleringsresultat {
        val spec = asSpec(specDto, pid())
        return simuleringClient.simulerAlderspensjon(spec)
    }

    private companion object {

        private const val PID_CLAIM_KEY = "pid"

        private fun pid() = extractPid(SecurityContextHolder.getContext().authentication)

        private fun extractPid(authentication: Authentication) = jwt(authentication).claims[PID_CLAIM_KEY] as String

        private fun jwt(authentication: Authentication) = authentication.credentials as Jwt
    }
}
