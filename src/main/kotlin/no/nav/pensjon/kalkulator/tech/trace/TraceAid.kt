package no.nav.pensjon.kalkulator.tech.trace

import io.opentelemetry.api.trace.Span
import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.tech.web.CustomHttpHeaders
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class TraceAid(private val callIdGenerator: CallIdGenerator) {
    fun begin(request: HttpServletRequest? = null) {
        val suppliedRequestId = request?.getHeader(CustomHttpHeaders.NAV_CORRELATION_ID)
            ?: request?.getHeader(CustomHttpHeaders.CALL_ID)

        MDC.put(CALL_ID_KEY, suppliedRequestId ?: callIdGenerator.newId())
    }

    fun callId(): String =
        MDC.get(CALL_ID_KEY) ?: callIdGenerator.newId().also { MDC.put(CALL_ID_KEY, it) }

    fun end() {
        MDC.clear()
    }

    companion object {
        private const val CALL_ID_KEY = "Nav-Call-Id"

        fun traceId(): String =
            Span.current().spanContext.traceId ?: "00000000000000000000000000000000"
    }
}