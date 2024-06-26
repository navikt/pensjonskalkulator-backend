package no.nav.pensjon.kalkulator.tjenestepensjon.api

import io.swagger.v3.oas.annotations.Operation
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.MedlemskapITjenestepensjonsordningDto
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.TjenestepensjonsforholdDto
import no.nav.pensjon.kalkulator.tjenestepensjon.api.map.TjenestepensjonMapper.toDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class TjenestepensjonController(
    private val service: TjenestepensjonService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @GetMapping("tpo-medlemskap")
    @Operation(
        summary = "Har offentlig tjenestepensjonsforhold",
        description = "Hvorvidt den innloggede brukeren har offentlig tjenestepensjonsforhold"
    )
    fun harTjenestepensjonsforhold(): TjenestepensjonsforholdDto {
        traceAid.begin()
        log.debug { "Request for tjenestepensjonsforhold-status" }

        return try {
            toDto(timed(service::harTjenestepensjonsforhold, "harTjenestepensjonsforhold"))
                .also { log.debug { "Tjenestepensjonsforhold-status respons: $it" } }
        } catch (e: EgressException) {
            handleError(e)!!
        } finally {
            traceAid.end()
        }
    }

    @GetMapping("v1/tpo-medlemskap")
    @Operation(
        summary = "Hent medlemskap i offentlige tjenestepensjonsordninger",
        description = "Henter både aktive og inaktive medlemskap til brukeren i offentlige tjenestepensjonsordninger"
    )
    fun hentMedlemskapITjenestepensjonsordninger(): MedlemskapITjenestepensjonsordningDto {
        traceAid.begin()
        log.debug { "Request for medlemskap i tjenestepensjonsordninger" }

        return try {
            toDto(timed(service::hentMedlemskapITjenestepensjonsordninger, "hentMedlemskapITjenestepensjonsordninger"))
                .also { log.debug { "Medlemskap i tjenestepensjonsordninger respons: $it" } }
        } catch (e: EgressException) {
            handleError(e)!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "feil ved henting av tjenestepensjonsforhold"
    }
}
