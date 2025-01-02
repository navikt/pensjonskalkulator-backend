package no.nav.pensjon.kalkulator.tech.metric

import io.micrometer.core.instrument.Metrics

object Metrics {
    private const val PREFIX = "pkb"

    fun countEgressCall(service: String, result: String) {
        Metrics
            .counter("$PREFIX-egress-call", "service", service, "result", result)
            .increment()
    }

    fun countEvent(eventName: String, result: String) {
        Metrics.counter("$PREFIX-$eventName", "result", result).increment()
    }

    fun countType(eventName: String, type: String) {
        Metrics.counter("$PREFIX-$eventName", "type", type).increment()
    }
}

object MetricResult {
    const val BAD_CLIENT = "bad-client"
    const val BAD_SERVER = "bad-server"
    const val BAD_XML = "bad-xml"
    const val OK = "ok"
}
