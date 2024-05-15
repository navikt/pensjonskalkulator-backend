package no.nav.pensjon.kalkulator.person.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.PersonService
import no.nav.pensjon.kalkulator.person.api.dto.ApiPersonDto
import no.nav.pensjon.kalkulator.person.api.dto.PersonV2
import no.nav.pensjon.kalkulator.person.api.map.PersonMapper.toDto
import no.nav.pensjon.kalkulator.person.api.map.PersonMapperV2.dtoV2
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class PersonController(
    private val service: PersonService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("v1/person")
    @Operation(
        summary = "Hent personinformasjon",
        description = "Henter personinformasjon for person-ID-en angitt i tilgangstokenet eller i HTTP-header."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av personinformasjon utført."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av personinformasjon kunne ikke utføres av tekniske årsaker.",
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

    @GetMapping("v2/person")
    @Operation(
        summary = "Hent personinformasjon",
        description = "Henter personinformasjon om den innloggede brukeren."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av personinformasjon utført."
            ),
            ApiResponse(
                responseCode = "404",
                description = "Personen ble ikke funnet."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av personinformasjon kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun personV2(): PersonV2 {
        traceAid.begin()
        log.debug { "Request for personinformasjon V2" }

        return try {
            dtoV2(timed(service::getPerson, "person"))
                .also { log.debug { "Personinformasjon respons: $it" } }
        } catch (e: NotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: EgressException) {
            handleError(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av personinformasjon"
    }
}
