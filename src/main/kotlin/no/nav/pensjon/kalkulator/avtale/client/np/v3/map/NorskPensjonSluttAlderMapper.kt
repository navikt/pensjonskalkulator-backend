package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.map.NorskPensjonPensjonsavtaleMapper.SLUTTMAANED_FORSKYVNING
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR

/**
 * Norsk Pensjon angir måned med verdi 1..12, og sluttmåned i en periode er "til", ikke "til og med".
 * I pensjonskalkulator angis måneder (i alder) med verdi 0..11, og måneder-verdi i sluttalder er "til og med".
 * Det betyr at Norsk Pensjons sluttmåned-verdi må fratrekkes 2 for å gi kalkulatorens sluttalder-måneder-verdi.
 * Et spesialtilfelle er hvis Norsk Pensjons sluttmåned-verdi er 1; da vil fratrekk av 2 gi negativ verdi (-1).
 * I dette tilfellet blir kalkulatorens alder-måneder-verdi isteden 11 (dvs. året før),
 * og Norsk Pensjons sluttalder-verdi må fratrekkes 1 for å gi kalkulatorens sluttår-verdi.
 * Eksempler:
 * Norsk Pensjon sluttalder = 70, sluttmåned = 12. Det gir kalkulator-sluttalder 70 år, 10 måneder.
 * Norsk Pensjon sluttalder = 70, sluttmåned = 2. Det gir kalkulator-sluttalder 70 år, 0 måneder.
 * Norsk Pensjon sluttalder = 70, sluttmåned = 1. Det gir kalkulator-sluttalder 69 år, 11 måneder.
 */
object NorskPensjonSluttAlderMapper {

    private const val DEFAULT_MAANED = 1 // Avtaler fra Norsk Pensjon har normalt startmåned 1

    fun sluttAar(sluttAlder: Int?, perioder: List<UtbetalingsperiodeDto>?): Int? =
        if (perioder?.let(::adjustSluttAar) == true)
            sluttAlder!! - 1
        else
            sluttAlder


    private fun adjustSluttAar(perioder: List<UtbetalingsperiodeDto>): Boolean {
        if (perioder.isEmpty() || perioder.any { it.sluttAlder == null }) {
            return false
        }

        return nullSafeMaaned(perioder.maxBy(::antallMaaneder).sluttMaaned) < SLUTTMAANED_FORSKYVNING
    }

    private fun antallMaaneder(periode: UtbetalingsperiodeDto): Int =
        periode.sluttAlder!! * MAANEDER_PER_AAR + nullSafeMaaned(periode.sluttMaaned) - 1

    private fun nullSafeMaaned(maaned: Int?): Int = maaned ?: DEFAULT_MAANED
}
