package no.nav.pensjon.kalkulator.person.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.api.dto.PersonDto
import no.nav.pensjon.kalkulator.person.api.map.PersonMapper.toDto
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.person.api.dto.ApiPersonDto
import no.nav.pensjon.kalkulator.person.api.map.PersonMapper.toV0Dto
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class PersonController(
    private val service: PersonService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {
    @GetMapping("v1/person")
    @Operation(
        summary = "Hent personinformasjon",
        description = "Henter personinformasjon om den innloggede brukeren"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av personinformasjon utført. I resultatet er verdi av 'maaneder' 0..11."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av personinformasjon kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun person(@RequestBody spec: UttaksalderIngressSpecDto?): ApiPersonDto {
        traceAid.begin()
        log.debug { "Request for personinformasjon: $spec" }

        return try {
            toDto(timed(service::getPerson, "person"))
                .also { log.debug { "Personinformasjon respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @GetMapping("person")
    @Operation(
        summary = "Hent personinformasjon V0",
        description = "Henter personinformasjon om den innloggede brukeren"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av personinformasjon utført. I resultatet er verdi av 'maaneder' 0..11."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av personinformasjon kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun personV0(@RequestBody spec: UttaksalderIngressSpecDto?): PersonDto {
        traceAid.begin()
        log.debug { "Request for personinformasjon V0: $spec" }

        return try {
            toV0Dto(timed(service::getPerson, "person"))
                .also { log.debug { "Personinformasjon V0 respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, "V0")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av personinformasjon"
    }
}
