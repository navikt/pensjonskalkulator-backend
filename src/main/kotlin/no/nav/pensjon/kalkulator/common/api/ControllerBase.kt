package no.nav.pensjon.kalkulator.common.api

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.BadRequestException
import no.nav.pensjon.kalkulator.tech.web.EgressException
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.lang.System.currentTimeMillis
import kotlin.reflect.KSuspendFunction0

abstract class ControllerBase(private val traceAid: TraceAid) {

    private val log = KotlinLogging.logger {}

    protected fun <R> timed(function: () -> R, functionName: String): R {
        val startTimeMillis = currentTimeMillis()
        val result = function()
        log.info { "$functionName took ${currentTimeMillis() - startTimeMillis} ms to process" }
        return result
    }

    protected fun <A, R> timed(function: (A) -> R, argument: A, functionName: String): R {
        val startTimeMillis = currentTimeMillis()
        val result = function(argument)
        log.info { "$functionName took ${currentTimeMillis() - startTimeMillis} ms to process" }
        return result
    }

    protected suspend fun <R> timed(function: KSuspendFunction0<R>, functionName: String): R {
        val startTimeMillis = currentTimeMillis()
        val result = function()
        log.info { "$functionName took ${currentTimeMillis() - startTimeMillis} ms to process" }
        return result
    }

    protected fun <T> badRequest(e: BadRequestException): T? {
        throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Call ID: ${traceAid.callId()} | Error: ${errorMessage()} | Details: ${extractMessageRecursively(e)}",
            e
        )
    }

    protected fun <T> handleError(e: EgressException, version: String = "V0") =
        if (e.isClientError) // "client" is here the backend server itself (calling other services)
            handleInternalError<T>(e, version)
        else
            handleExternalError<T>(e, version)

    abstract fun errorMessage(): String

    private fun <T> handleInternalError(e: EgressException, version: String): T? {
        logError(e, "Intern", version)

        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Call ID: ${traceAid.callId()} | Error: ${errorMessage()} | Details: ${extractMessageRecursively(e)}",
            e
        )
    }

    private fun <T> handleExternalError(e: EgressException, version: String): T? {
        logError(e, "Ekstern", version)
        return serviceUnavailable(e)
    }

    private fun <T> serviceUnavailable(e: EgressException): T? {
        throw ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Call ID: ${traceAid.callId()} | Error: ${errorMessage()} | Details: ${extractMessageRecursively(e)}",
            e
        )
    }

    private fun logError(e: EgressException, category: String, version: String) {
        log.error(e) { "$category ${errorMessage()} $version: ${extractMessageRecursively(e)}" }
    }

    private fun extractMessageRecursively(ex: Throwable): String {
        val builder = StringBuilder()
        builder.append(ex.message)

        if (ex.cause == null) {
            return builder.toString()
        }

        builder.append(" | Cause: ").append(extractMessageRecursively(ex.cause!!))
        return builder.toString()
    }

    protected companion object {
        @Language("json")
        const val SERVICE_UNAVAILABLE_EXAMPLE = """{
    "timestamp": "2023-09-12T10:37:47.056+00:00",
    "status": 503,
    "error": "Service Unavailable",
    "message": "En feil inntraff",
    "path": "/api/ressurs"
}"""
    }
}
