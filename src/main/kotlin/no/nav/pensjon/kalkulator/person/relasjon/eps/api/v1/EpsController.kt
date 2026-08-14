package no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstatus
import no.nav.pensjon.kalkulator.person.relasjon.eps.EpsService
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.*
import no.nav.pensjon.kalkulator.person.relasjon.eps.api.v1.acl.FamilierelasjonMapper.toDto
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.PopulasjonstilgangNektetException
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.Populasjonstilgangsnekt
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/intern")
class EpsController(
    private val service: EpsService,
    private val traceAid: TraceAid,
    private val auditor: Auditor
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
    fun naavaerendeSivilstatus(): EpsV1SivilstatusResult {
        traceAid.begin()

        return try {
            EpsV1SivilstatusResult(sivilstatus = CommonV1Sivilstatus.fromInternalValue(service.naavaerendeSivilstatus()))
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
                responseCode = "400",
                description = "Henting av EPS kunne ikke utføres pga. mangelfull spesifikasjon."
            ),
            ApiResponse(
                responseCode = "403",
                description = "Henting av EPS kunne ikke utføres pga. manglende tilganger."
            ),
            ApiResponse(
                responseCode = "503",
                description = "Henting av EPS kunne ikke utføres av tekniske årsaker.",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            )
        ]
    )
    fun nyligsteEps(@RequestBody spec: EpsV1EpsSpec): ResponseEntity<EpsV1Familierelasjon> {
        traceAid.begin()

        return try {
            val relasjon = service.nyligsteRelasjon(sivilstatus = sivilstatus(spec))
            relasjon.pid?.let { audit(pid = it, bakgrunn = spec.bakgrunn) }
            ResponseEntity.status(HttpStatus.OK).body(toDto(source = relasjon))
        } catch (e: PopulasjonstilgangNektetException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(tilgangNektet(e))
        } catch (e: BadRequestException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultForMangelfullSpesifikasjon(e))
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    private fun audit(pid: Pid, bakgrunn: String?) {
        auditor.audit(
            onBehalfOfPid = pid,
            requestUri = "intern/v1/eps",
            message = bakgrunn
        )
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "eps-feil"

        private fun sivilstatus(spec: EpsV1EpsSpec): Sivilstatus =
            spec.sivilstand?.internalValue?.sivilstatus
                ?: spec.sivilstatus?.internalValue
                ?: throw BadRequestException("sivilstand eller sivilstatus ikke angitt")

        private fun tilgangNektet(e: PopulasjonstilgangNektetException): EpsV1Familierelasjon =
            problemResult(
                type = EpsV1ProblemType.TILGANG_NEKTET,
                beskrivelse = "Ikke tilgang til personen",
                tilgangsnekt = e.aarsak
            )

        private fun resultForMangelfullSpesifikasjon(e: BadRequestException): EpsV1Familierelasjon =
            problemResult(type = EpsV1ProblemType.MANGELFULL_SPESIFIKASJON, beskrivelse = e.message)

        private fun problemResult(
            type: EpsV1ProblemType,
            beskrivelse: String? = null,
            tilgangsnekt: Populasjonstilgangsnekt? = null
        ) =
            EpsV1Familierelasjon(
                pid = null,
                fom = null,
                relasjonstype = EpsV1Relasjonstype.UKJENT,
                relasjonPersondata = null,
                problem = EpsV1Problem(
                    type,
                    beskrivelse ?: "ukjent",
                    tilgangsnekt = tilgangsnekt?.let(::tilgangsnekt)
                )
            )

        private fun tilgangsnekt(source: Populasjonstilgangsnekt) =
            EpsV1Tilgangsnekt(
                aarsak = EpsV1AvvisningAarsak.fromInternalValue(source.aarsak),
                begrunnelse = source.begrunnelse
            )
    }
}