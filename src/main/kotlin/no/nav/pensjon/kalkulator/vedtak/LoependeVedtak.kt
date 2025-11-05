package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.person.Sivilstand
import java.math.BigDecimal
import java.time.LocalDate

data class LoependeVedtak(
    val alderspensjon: LoependeAlderspensjonDetaljer?,
    val fremtidigLoependeVedtakAp: FremtidigAlderspensjonDetaljer?,
    val ufoeretrygd: LoependeUfoeretrygdDetaljer?,
    val afpPrivat: LoependeVedtakDetaljer?,
    val afpOffentlig: LoependeVedtakDetaljer?,
    val pre2025OffentligAfp: LoependeVedtakDetaljer? = null,
    val gjeldendeUttaksgradFom: LocalDate? = null
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
    var utbetalingSisteMaaned: Utbetaling? = null,
    val sivilstand: Sivilstand,
)

data class Utbetaling(
    val beloep: BigDecimal?,
    val posteringsdato: LocalDate,
)

data class FremtidigAlderspensjonDetaljer(
    val grad: Int,
    val fom: LocalDate,
    val sivilstand: Sivilstand,
)
