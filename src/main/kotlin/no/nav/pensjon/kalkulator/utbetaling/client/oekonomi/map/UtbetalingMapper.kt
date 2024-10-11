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
                        erUtbetalt = "18" == utbetaling.utbetalingsstatus, //UTBETALT_AV_BANK
                        gjelderAlderspensjon = "ALDERSPENSJON".contentEquals(it.ytelsestype, ignoreCase = true),
                    )
                }.toList()
        }.flatten().toList()
    }
}