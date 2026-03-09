package no.nav.pensjon.kalkulator.person.api.v7

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.PersonFacade
import no.nav.pensjon.kalkulator.person.api.v7.acl.PersonResultMapper.toDto
import no.nav.pensjon.kalkulator.person.api.v7.acl.PersonV7Result
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * Endpoint for version 7 of the REST service for person data.
 * Including 'V7' in the class name to avoid bean name clash with other versions.
 */
@RestController
@RequestMapping("api")
@SecurityRequirement(name = "BearerAuthentication")
class PersonV7Controller(
    private val service: PersonFacade,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    @GetMapping("v7/person")
    @Operation(
        summary = "Hent personinformasjon",
        description = "Henter informasjon om personen hvis person-ID er angitt enten i bearer-tokenet eller som fnr-header."
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
            )
        ]
    )
    fun person(): PersonV7Result {
        traceAid.begin()

        return try {
            toDto(service.getPerson())
        } catch (e: NotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: EgressException) {
            handleError(e, "V7")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av personinformasjon"
    }
}
