package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.map

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.HentUtbetalingerRequestDto
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.OekonomiUtbetalingDto
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.OekonomiYtelsesPeriodeDto
import java.time.LocalDate

object UtbetalingMapper {

    fun fromDto(dto: List<OekonomiUtbetalingDto>) = dto
        .map { mapToUtbetalinger(it) }
        .flatten()
        .toList()

    private fun mapToUtbetalinger(utbetaling: OekonomiUtbetalingDto) =
        utbetaling.ytelseListe
            .map {
                Utbetaling(
                    utbetalingsdato = utbetaling.utbetalingsdato,
                    posteringsdato = utbetaling.posteringsdato,
                    beloep = it.ytelseskomponentersum,
                    erUtbetalt = utbetaling.utbetalingsstatus == UTBETALT_AV_BANK,
                    gjelderAlderspensjon = YTELSESTYPE_FOR_ALDERSPENSJON.contentEquals(
                        it.ytelsestype,
                        ignoreCase = true
                    ),
                    fom = it.ytelsesperiode.fom,
                    tom = it.ytelsesperiode.tom,
                )
            }.toList()

    fun toDto(pid: Pid): HentUtbetalingerRequestDto {
        val foersteDagForrigeMaaned = LocalDate.now().minusMonths(1).withDayOfMonth(1)
        val sisteDagForrigeMaaned = foersteDagForrigeMaaned.withDayOfMonth(foersteDagForrigeMaaned.lengthOfMonth())

        return HentUtbetalingerRequestDto(
            ident = pid.value,
            rolle = UTBETALINGSMOTTAKER,
            periode = OekonomiYtelsesPeriodeDto(foersteDagForrigeMaaned, sisteDagForrigeMaaned),
            periodetype = PERIODE_TYPE,
        )
    }

    const val UTBETALT_AV_BANK = "18"
    const val YTELSESTYPE_FOR_ALDERSPENSJON = "Alderspensjon"
    const val UTBETALINGSMOTTAKER: String = "UTBETALT_TIL"
    const val PERIODE_TYPE: String = "YTELSESPERIODE"
}