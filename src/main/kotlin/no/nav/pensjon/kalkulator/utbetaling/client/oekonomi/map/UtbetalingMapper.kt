package no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.map

import no.nav.pensjon.kalkulator.utbetaling.Utbetaling
import no.nav.pensjon.kalkulator.utbetaling.client.oekonomi.dto.OekonomiUtbetalingDto

object UtbetalingMapper {

    fun fromDto(dto: List<OekonomiUtbetalingDto>): List<Utbetaling> {
        return dto.map { utbetaling ->
            utbetaling.ytelseListe
                .map {
                    Utbetaling(
                        utbetalingsdato = utbetaling.utbetalingsdato,
                        posteringsdato = utbetaling.posteringsdato,
                        beloep = it.ytelseskomponentersum,
                        erUtbetalt = utbetaling.utbetalingsstatus == UTBETALT_AV_BANK,
                        gjelderAlderspensjon = YTELSESTYPE_FOR_ALDERSPENSJON.contentEquals(it.ytelsestype, ignoreCase = true),
                        fom = it.ytelsesperiode.fom,
                        tom = it.ytelsesperiode.tom,
                    )
                }.toList()
        }.flatten().toList()
    }

    const val UTBETALT_AV_BANK = "18"
    const val YTELSESTYPE_FOR_ALDERSPENSJON = "Alderspensjon"
}