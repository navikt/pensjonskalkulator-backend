package no.nav.pensjon.kalkulator.tech.time

import mu.KotlinLogging
import java.lang.System.currentTimeMillis

open class Timed {

    private val log = KotlinLogging.logger {}

    fun <R> timed(function: () -> R, functionName: String): R {
        val startTimeMillis = currentTimeMillis()
        val result = function()
        log.info { "$functionName took ${currentTimeMillis() - startTimeMillis} ms to process" }
        return result
    }

    fun <A, R> timed(function: (A) -> R, argument: A, functionName: String): R {
        val startTimeMillis = currentTimeMillis()
        val result = function(argument)
        log.info { "$functionName took ${currentTimeMillis() - startTimeMillis} ms to process" }
        return result
    }
}