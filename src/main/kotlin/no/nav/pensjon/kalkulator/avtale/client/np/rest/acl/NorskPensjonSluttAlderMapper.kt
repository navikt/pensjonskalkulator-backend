package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

/**
 * Norsk Pensjon angir sluttalder "til", ikke "til og med".
 * I pensjonskalkulator viser vi sluttalderen som "til og med".
 * Sluttalder justeres kun når alle utbetalingsperiodene har sluttdato.
 */
object NorskPensjonSluttAlderMapper {
    private val log = KotlinLogging.logger {}

    fun sluttAar(sluttAlder: Int?, perioder: List<UtbetalingsperiodeDto>, foedselsdato: LocalDate): Int? =
        sluttAlder
            ?.let { if (perioder.isEmpty()) return sluttAlder }
            ?.let { if (perioder.any { it.datoTom == null }) return sluttAlder }
            ?.let { perioder.mapNotNull { periode -> periode.datoTom }.maxOrNull() ?: return sluttAlder }
            ?.let { datoTom -> Alder.from(foedselsdato, datoTom)
                .also { log.warn { "Norsk pensjon REST: justerer sluttalder fra $sluttAlder til ${it.aar}" } } }
            ?.aar
}