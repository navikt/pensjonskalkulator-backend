package no.nav.pensjon.kalkulator.tech.crypto.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.api.ControllerBase
import no.nav.pensjon.kalkulator.tech.crypto.CryptoService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api")
class CryptoController(
    private val service: CryptoService,
    private val traceAid: TraceAid
) : ControllerBase(traceAid) {

    private val log = KotlinLogging.logger {}

    @PostMapping("v1/encrypt")
    @Operation(
        summary = "Krypter tekst",
        description = "Krypterer angitt tekst (typisk brukstilfelle er for fødselsnumre)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Kryptering utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Kryptering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun encrypt(@RequestBody text: String): String {
        traceAid.begin()
        log.debug { "Request for encryption: $text" }

        return try {
            timed(service::encrypt, text, "encrypt")
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    @PostMapping("v1/decrypt")
    @Operation(
        summary = "Dekrypter tekst",
        description = "Dekrypterer angitt tekst (typisk brukstilfelle er for fødselsnumre)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Dekryptering utført"
            ),
            ApiResponse(
                responseCode = "503", description = "Dekryptering kunne ikke utføres av tekniske årsaker",
                content = [Content(examples = [ExampleObject(value = SERVICE_UNAVAILABLE_EXAMPLE)])]
            ),
        ]
    )
    fun decrypt(@RequestBody text: String): String {
        traceAid.begin()
        log.debug { "Request for decryption: $text" }

        return try {
            timed(service::decrypt, text, "decrypt")
        } catch (e: EgressException) {
            handleError(e, "V1")!!
        } finally {
            traceAid.end()
        }
    }

    override fun errorMessage() = ERROR_MESSAGE

    private companion object {
        private const val ERROR_MESSAGE = "krypto-feil"
    }
}
