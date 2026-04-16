package no.nav.pensjon.kalkulator.ansatt.enhet.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.ansatt.enhet.EnhetService
import no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl.AnsattEnhetV1Problem
import no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl.AnsattEnhetV1ProblemType
import no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl.AnsattEnhetV1Result
import no.nav.pensjon.kalkulator.ansatt.enhet.api.v1.acl.ResultMapper.toDto
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/intern")
class AnsattEnhetV1Controller(
    private val service: EnhetService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("v1/enheter")
    @Operation(
        summary = "Hent den ansattes enheter",
        description = "Henter en liste over den innloggede ansattes enheter, dvs. tjenestekontornummer."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av enheter utført."
            ),
            ApiResponse(
                responseCode = "401",
                description = "Henting av enheter kunne ikke utføres pga. manglende/feilaktig autentisering."
            ),
            ApiResponse(
                responseCode = "403",
                description = "Henting av enheter kunne ikke utføres pga. manglende tilgang til tjenesten eller personen."
            ),
            ApiResponse(
                responseCode = "404",
                description = "Henting av enheter kunne ikke utføres fordi angitt ansatt-ID ikke finnes i systemet."
            ),
            ApiResponse(
                responseCode = "500",
                description = "Henting av enheter kunne ikke utføres pga. feil i systemet."
            )
        ]
    )
    fun tjenestekontorEnhetListe(): ResponseEntity<AnsattEnhetV1Result> =
        try {
            traceAid.begin()
            val result = toDto(service.tjenestekontorEnhetListe())
            ResponseEntity.status(result.problem?.kode?.httpStatus ?: HttpStatus.OK).body(result)
        } catch (e: Exception) {
            log.error(e) { "Intern feil" }
            throw e
        } finally {
            traceAid.end()
        }

    override fun errorMessage() = ERROR_MESSAGE

    @ExceptionHandler(value = [Exception::class])
    private fun internalError(e: Exception): ResponseEntity<AnsattEnhetV1Result> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem(e))

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av ansattes enheter"

        private fun problem(e: Exception) =
            AnsattEnhetV1Result(
                enhetListe = emptyList(),
                problem = AnsattEnhetV1Problem(
                    kode = AnsattEnhetV1ProblemType.SERVERFEIL,
                    beskrivelse = e.message ?: e.javaClass.simpleName
                )
            )
    }
}
