package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

/**
 * Norsk Pensjon angir sluttalder "til", ikke "til og med".
 * I pensjonskalkulator viser vi sluttalderen som "til og med".
 * Sluttalder justeres kun når alle utbetalingsperiodene har sluttdato.
 *
 * Sluttalder beregnes ved 1. dag i den siste utbetalingsmåneden.
 * Brukeren får alltid utbetaling i t.o.m. alderen vi viser,
 * og kanskje en måned ekstra om fødselsdagen kommer før utbetalingsdagen i måneden.
 *
 * En helt nøyaktig sluttalder krever at utbetalingsdagen tas med i beregningen.
 * Det vurderes som unødvendig detaljert gitt pensjonskalkulatorens tidshorisont på 10+ år.
 */
object NorskPensjonSluttAlderMapper {

    fun sluttAar(sluttAlder: Int?, perioder: List<UtbetalingsperiodeDto>, foedselsdato: LocalDate): Int? =
        sluttAlder
            ?.let { if (perioder.isEmpty()) return sluttAlder }
            ?.let { if (perioder.any { it.datoTom == null }) return sluttAlder }
            ?.let { perioder.mapNotNull { periode -> periode.datoTom }.maxOrNull() ?: return sluttAlder }
            ?.let { datoTom -> Alder.from(foedselsdato, datoTom.withDayOfMonth(1)) }
            ?.aar
}