package no.nav.pensjon.kalkulator.vedtak

import java.math.BigDecimal
import java.time.LocalDate

data class LoependeVedtak(
    val alderspensjon: LoependeAlderspensjonDetaljer?,
    val fremtidigLoependeVedtakAp: Boolean,
    val ufoeretrygd: LoependeUfoeretrygdDetaljer?,
    val afpPrivat: LoependeVedtakDetaljer?,
    val afpOffentlig: LoependeVedtakDetaljer?,
    val afpOffentligForBrukereFoedtFoer1963: LoependeVedtakDetaljer? = null,
)

data class LoependeUfoeretrygdDetaljer(
    val grad: Int,
    val fom: LocalDate,
)

data class LoependeVedtakDetaljer(
    val fom: LocalDate,
)

data class LoependeAlderspensjonDetaljer(
    val grad: Int,
    val fom: LocalDate,
    var utbetalingSisteMaaned: UtbetalingSisteMaaned? = null,
)

data class UtbetalingSisteMaaned(
    val beloep: BigDecimal?,
    val posteringsdato: LocalDate,
)
