package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.EpsSpecDto
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.FamilierelasjonDto
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.FamilierelasjonMapper
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class EpsController(
    private val service: EpsService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/nyligste-eps")
    @Operation(
        summary = "Hent nyligste EPS",
        description = "Henter informasjon om nyligste ektefelle/partner/samboer."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av EPS utført."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av EPS kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            )
        ]
    )
    fun nyligsteEps(@RequestBody spec: EpsSpecDto): FamilierelasjonDto {
        traceAid.begin()
        log.debug { "Request for nyligste EPS" }

        return try {
            FamilierelasjonMapper.toDto(
                source = service.nyligsteEps(
                    sivilstatus = spec.sivilstatus.internalValue
                )
            )
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "eps-feil"
    }
}
