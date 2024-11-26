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
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonV2
import no.nav.pensjon.kalkulator.person.api.map.PersonMapperV2
import no.nav.pensjon.kalkulator.person.api.map.PersonMapperV4
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
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

    @PostMapping("v2/person")
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
            ),
        ]
    )
    fun personV2(): PersonV2 {
        traceAid.begin()
        log.debug { "Request for personinformasjon V2" }

        return try {
            PersonMapperV2.dtoV2(timed(service::getPerson, "person"))
                .also { log.debug { "Personinformasjon respons: $it" } }
        } catch (e: NotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: EgressException) {
            handleError(e, "V2")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v4/person")
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
            ),
        ]
    )
    fun personV4(): PersonResultV4 {
        traceAid.begin()
        log.debug { "Request for personinformasjon V4" }

        return try {
            PersonMapperV4.dtoV4(timed(service::getPerson, "person"))
                .also { log.debug { "Personinformasjon respons: $it" } }
        } catch (e: NotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: EgressException) {
            handleError(e, "V4")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av personinformasjon"
    }
}
