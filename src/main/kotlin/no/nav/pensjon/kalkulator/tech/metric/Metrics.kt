package no.nav.pensjon.kalkulator.tech.metric

import io.micrometer.core.instrument.Metrics

object Metrics {
    private const val PREFIX = "pkb-"

     fun countEvent(eventName: String, result: String) {
        Metrics.counter("$PREFIX$eventName", "result", result).increment()
     }
}

object MetricResult {
    const val BAD_CLIENT = "bad-client"
    const val BAD_OTHER = "bad-other"
    const val BAD_SERVER = "bad-server"
    const val BAD_XML = "bad-xml"
    const val OK = "ok"
}
