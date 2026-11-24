package no.nav.pensjon.kalkulator.tjenestepensjon.api

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.HarLivsvarigOffentligAfpDtoV1
import no.nav.pensjon.kalkulator.tjenestepensjon.api.map.HarLivsvarigOffentligAfpV1DtoMapper.toDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api")
class LivsvarigOffentligAfpStatusController(
    private val service: TjenestepensjonService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("v1/har-livsvarig-offentlig-afp")
    @Operation(
        summary = "Livsvarig offentlig AFP status",
        description = "Hvorvidt den innloggede brukeren har livsvarig offentlig AFP"
    )
    fun harLivsvarigOffentligAfpV1(): HarLivsvarigOffentligAfpDtoV1 {
        traceAid.begin()
        val versjon = "V1"
        log.debug { "Request for livsvarig offentlig AFP $versjon" }

        return try {
            toDto(timed(service::harAfpOffentligLivsvarig, "harLivsvarigOffentligAfp$versjon"))
                .also { log.debug { "livsvarig offentlig AFP respons: $it" } }
        } catch (e: EgressException) {
            handleError(e, versjon)!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av livsvarig offentlig afp status"
    }
}