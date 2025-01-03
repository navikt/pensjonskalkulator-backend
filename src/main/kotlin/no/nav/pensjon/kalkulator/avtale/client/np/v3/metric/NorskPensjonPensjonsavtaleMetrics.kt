package no.nav.pensjon.kalkulator.avtale.client.np.v3.metric

import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.PensjonsrettighetDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto
import no.nav.pensjon.kalkulator.tech.metric.Metrics.countEvent

object NorskPensjonPensjonsavtaleMetrics {

    private const val KATEGORIER_UTEN_UTBETALINGSPERIODER_EVENT_NAME = "avtalekategorierutenperioder"
    private const val UNDERKATEGORIER_EVENT_NAME = "avtaleunderkategorier"
    private const val MAANEDER_EVENT_NAME = "avtaleperiodemaaneder"
    private const val MANGLENDE_SLUTTMAANED_INDIKATOR = ">"
    private const val MANGLENDE_KATEGORI_INDIKATOR = "UDEFINERT"

    fun updateMetrics(envelope: EnvelopeDto) {
        envelope.body?.pensjonsrettigheter?.pensjonsRettigheter?.let(::countInterestingEvents)
    }

    private fun countInterestingEvents(rettigheter: List<PensjonsrettighetDto>) {
        countKategorier(rettigheter)
        countKategorierUtenUtbetalingsperioder(rettigheter)
        countMaaneder(rettigheter)
    }

    /**
     * Teller forekomster av underkategorier.
     * Utelater hovedkategorier (individuelleOrdninger, offentligTjenestepensjon, privatTjenestepensjon).
     */
    private fun countKategorier(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter.forEach {
            countEvent(
                eventName = UNDERKATEGORIER_EVENT_NAME,
                result = it.underkategori ?: MANGLENDE_KATEGORI_INDIKATOR
            )
        }
    }

    private fun countKategorierUtenUtbetalingsperioder(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .filter { it.utbetalingsperioder.orEmpty().isEmpty() }
            .forEach {
                countEvent(
                    eventName = KATEGORIER_UTEN_UTBETALINGSPERIODER_EVENT_NAME,
                    result = "${it.kategori ?: MANGLENDE_KATEGORI_INDIKATOR}:${it.underkategori ?: MANGLENDE_KATEGORI_INDIKATOR}"
                )
            }
    }

    private fun countMaaneder(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .flatMap { it.utbetalingsperioder.orEmpty() }
            .forEach(::countMaaneder)
    }

    private fun countMaaneder(periode: UtbetalingsperiodeDto) {
        countEvent(eventName = MAANEDER_EVENT_NAME, result = "${periode.startMaaned}-${sluttMaaned(periode)}")
    }

    private fun sluttMaaned(periode: UtbetalingsperiodeDto) = periode.sluttMaaned ?: MANGLENDE_SLUTTMAANED_INDIKATOR
}
