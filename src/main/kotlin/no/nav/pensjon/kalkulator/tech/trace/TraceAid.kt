package no.nav.pensjon.kalkulator.tech.trace

import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class TraceAid(private val callIdGenerator: CallIdGenerator) {

    fun initialize() {
        MDC.put(CALL_ID_KEY, callIdGenerator.newId())
    }

    fun callId(): String =
        MDC.get(CALL_ID_KEY) ?: callIdGenerator.newId().also { MDC.put(CALL_ID_KEY, it) }

    fun finalize() {
        MDC.clear()
    }

    private companion object {
        private const val CALL_ID_KEY = "Nav-Call-Id"
    }
}
