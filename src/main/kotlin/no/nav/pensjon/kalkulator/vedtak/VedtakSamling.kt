package no.nav.pensjon.kalkulator.vedtak

import no.nav.pensjon.kalkulator.person.Sivilstand
import java.math.BigDecimal
import java.time.LocalDate

data class VedtakSamling(
    val loependeAlderspensjon: LoependeAlderspensjon?,
    val fremtidigAlderspensjon: FremtidigAlderspensjon?,
    val ufoeretrygd: LoependeUfoeretrygd?,
    val privatAfp: LoependeEntitet?,
    val pre2025OffentligAfp: LoependeEntitet? = null
) {
    fun withAlderspensjonUtbetalingSisteMaaned(utbetaling: Utbetaling) =
        VedtakSamling(
            loependeAlderspensjon = loependeAlderspensjon?.withUtbetalingSisteMaaned(utbetaling),
            fremtidigAlderspensjon = fremtidigAlderspensjon,
            ufoeretrygd = ufoeretrygd,
            privatAfp = privatAfp,
            pre2025OffentligAfp = pre2025OffentligAfp
        )

     fun hasContent(): Boolean =
        loependeAlderspensjon != null &&
                fremtidigAlderspensjon != null &&
                ufoeretrygd != null &&
                privatAfp != null &&
                pre2025OffentligAfp != null
}

data class LoependeAlderspensjon(
    val grad: Int,
    val fom: LocalDate,
    val uttaksgradFom: LocalDate? = null,
    val utbetalingSisteMaaned: Utbetaling? = null,
    val sivilstand: Sivilstand
) {
    fun withUtbetalingSisteMaaned(utbetaling: Utbetaling) =
        LoependeAlderspensjon(
            grad = grad,
            fom = fom,
            uttaksgradFom = uttaksgradFom,
            utbetalingSisteMaaned = utbetaling,
            sivilstand = sivilstand
        )
}

data class FremtidigAlderspensjon(
    val grad: Int,
    val fom: LocalDate,
    val sivilstand: Sivilstand
)

data class LoependeUfoeretrygd(
    val grad: Int,
    val fom: LocalDate
)

data class LoependeEntitet(
    val fom: LocalDate
)

data class Utbetaling(
    val beloep: BigDecimal?,
    val posteringsdato: LocalDate
)
