package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith

class NorskPensjonResultTest : ShouldSpec({

    should("serialize NorskPensjonResult to JSON") {
        val result = NorskPensjonResult().apply {
            pensjonsRettigheter = listOf(NorskPensjonPensjonsrettighet())
            utilgjengeligeInnretninger = emptyList()
        }.toString()

        result shouldStartWith "{"
        result shouldContain "\"pensjonsRettigheter\":[{"
        result shouldContain "\"utilgjengeligeInnretninger\":[]"
    }

    should("serialize NorskPensjonPensjonsrettighet to JSON with escaped text") {
        val result = NorskPensjonPensjonsrettighet().apply {
            avtalenummer = "123"
            arbeidsgiver = "Eksempel \"AS\""
        }.toString()

        result shouldStartWith "{"
        result shouldContain "\"avtalenummer\":\"123\""
        result shouldContain "\"arbeidsgiver\":\"Eksempel \\\"AS\\\"\""
    }

    should("serialize NorskPensjonUtilgjengeligInnretning to JSON") {
        val result = NorskPensjonUtilgjengeligInnretning().apply {
            selskapsnavn = "Eksempel"
            heltUtilgjengelig = true
        }.toString()

        result shouldStartWith "{"
        result shouldContain "\"selskapsnavn\":\"Eksempel\""
        result shouldContain "\"heltUtilgjengelig\":true"
    }
})

