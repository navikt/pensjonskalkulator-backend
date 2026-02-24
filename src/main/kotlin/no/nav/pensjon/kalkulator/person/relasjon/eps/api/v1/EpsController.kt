package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.*
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/intern")
class EpsController(
    private val service: EpsService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    @GetMapping("v1/sivilstatus")
    @Operation(
        summary = "Hent nåværende sivilstatus",
        description = "Henter informasjon om nåværende sivilstatus."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Henting av sivilstatus utført."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av sivilstatus kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            )
        ]
    )
    fun naavaerendeSivilstatus(): SivilstatusResultDto {
        traceAid.begin()

        return try {
            SivilstatusResultDto(sivilstatus = SivilstatusDto.fromInternalValue(service.naavaerendeSivilstatus()))
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v1/eps")
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

        return try {
            FamilierelasjonMapper.toDto(
                source = service.nyligsteRelasjon(
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
