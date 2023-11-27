package no.nav.pensjon.kalkulator.avtale.client.np.v3.metric

import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.PensjonsrettighetDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto
import no.nav.pensjon.kalkulator.tech.metric.Metrics

object NorskPensjonPensjonsavtaleMetrics {

    private const val MAANEDER_METRIC_NAME = "avtaleperiodemaaneder"
    private const val MANGLENDE_SLUTTMAANED_INDIKATOR = ">"

    fun updateMetrics(envelope: EnvelopeDto) {
        envelope.body?.pensjonsrettigheter?.pensjonsRettigheter?.let(::countMaaneder)
    }

    private fun countMaaneder(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .flatMap { it.utbetalingsperioder ?: emptyList() }
            .forEach(this::countMaaneder)
    }

    private fun countMaaneder(periode: UtbetalingsperiodeDto) {
        Metrics.countEvent(MAANEDER_METRIC_NAME, "${periode.startMaaned}-${sluttMaaned(periode)}")
    }

    private fun sluttMaaned(periode: UtbetalingsperiodeDto) = periode.sluttMaaned ?: MANGLENDE_SLUTTMAANED_INDIKATOR
}
