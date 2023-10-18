package no.nav.pensjon.kalkulator.avtale.client.np.v3.metric

import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.PensjonsrettighetDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto
import no.nav.pensjon.kalkulator.tech.metric.Metrics

object NorskPensjonPensjonsavtaleMetrics {

    private const val MAANEDER_METRIC_NAME = "avtaleperiodemaaneder"
    private const val AVTALER_UTEN_START_METRIC_NAME = "avtaler_uten_start"
    private const val MANGLENDE_SLUTTMAANED_INDIKATOR = ">"
    private const val DEFAULT_KATEGORI = "null"

    fun updateMetrics(envelope: EnvelopeDto) {
        envelope.body?.pensjonsrettigheter?.pensjonsRettigheter?.let {
            countAvtalerUtenStart(it)
            countMaaneder(it)
        }
    }

    private fun countAvtalerUtenStart(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .filter { it.startAlder == null }
            .forEach(::countKategori)
    }

    private fun countMaaneder(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .flatMap { it.utbetalingsperioder ?: emptyList() }
            .forEach(this::countMaaneder)
    }

    private fun countKategori(rettighet: PensjonsrettighetDto) {
        Metrics.countEvent(AVTALER_UTEN_START_METRIC_NAME, "${kategori(rettighet)}-${underkategori(rettighet)}")
    }

    private fun countMaaneder(periode: UtbetalingsperiodeDto) {
        Metrics.countEvent(MAANEDER_METRIC_NAME, "${periode.startMaaned}-${sluttMaaned(periode)}")
    }

    private fun kategori(rettighet: PensjonsrettighetDto) = rettighet.kategori ?: DEFAULT_KATEGORI

    private fun underkategori(rettighet: PensjonsrettighetDto) = rettighet.underkategori ?: DEFAULT_KATEGORI

    private fun sluttMaaned(periode: UtbetalingsperiodeDto) = periode.sluttMaaned ?: MANGLENDE_SLUTTMAANED_INDIKATOR
}
