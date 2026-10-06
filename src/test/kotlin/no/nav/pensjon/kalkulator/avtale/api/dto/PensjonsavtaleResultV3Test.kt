package no.nav.pensjon.kalkulator.avtale.api.dto

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import no.nav.pensjon.kalkulator.general.Alder

class PensjonsavtaleResultV3Test : ShouldSpec({

    val periode = UtbetalingsperiodeV3(
        startAlder = Alder(67, 0),
        sluttAlder = Alder(77, 0),
        aarligUtbetaling = 80_000,
        grad = 100
    )
    val avtale = PensjonsavtaleV3(
        produktbetegnelse = "Pensjon \"Pluss\"",
        kategori = AvtaleKategoriV3.PRIVAT_TJENESTEPENSJON,
        startAar = 67,
        sluttAar = 77,
        utbetalingsperioder = listOf(periode)
    )
    val selskap = SelskapV3("Eksempel", true)

    should("serialize PensjonsavtaleResultV3 to JSON") {
        val json = PensjonsavtaleResultV3(listOf(avtale), listOf(selskap)).toString()

        json shouldStartWith "{"
        json shouldContain "\"avtaler\":[{"
        json shouldContain "\"utilgjengeligeSelskap\":[{"
    }

    should("serialize PensjonsavtaleV3 to JSON with escaped text") {
        val json = avtale.toString()

        json shouldContain "\"produktbetegnelse\":\"Pensjon \\\"Pluss\\\"\""
        json shouldContain "\"utbetalingsperioder\":[{"
    }

    should("serialize SelskapV3 to JSON") {
        selskap.toString() shouldContain "\"heltUtilgjengelig\":true"
    }

    should("serialize UtbetalingsperiodeV3 to JSON") {
        periode.toString() shouldContain "\"aarligUtbetaling\":80000"
    }
})

