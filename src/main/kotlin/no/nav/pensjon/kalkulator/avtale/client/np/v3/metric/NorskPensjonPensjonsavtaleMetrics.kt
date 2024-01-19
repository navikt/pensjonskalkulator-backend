package no.nav.pensjon.kalkulator.avtale.client.np.v3.metric

import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.PensjonsrettighetDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto
import no.nav.pensjon.kalkulator.tech.metric.Metrics.countEvent

object NorskPensjonPensjonsavtaleMetrics {

    private const val KATEGORIER_EVENT_NAME = "avtalekategorier"
    private const val UNDERKATEGORIER_EVENT_NAME = "avtaleunderkategorier"
    private const val MAANEDER_EVENT_NAME = "avtaleperiodemaaneder"
    private const val PERIODE_ANTALL_EVENT_NAME = "avtaleperiodeantall"
    private const val MANGLENDE_SLUTTMAANED_INDIKATOR = ">"
    private const val MANGLENDE_KATEGORI_INDIKATOR = "UDEFINERT"

    fun updateMetrics(envelope: EnvelopeDto) {
        envelope.body?.pensjonsrettigheter?.pensjonsRettigheter?.let(::countInterestingEvents)
    }

    private fun countInterestingEvents(rettigheter: List<PensjonsrettighetDto>) {
        countKategorier(rettigheter)
        countMaaneder(rettigheter)
        countPeriodeAntall(rettigheter)
    }

    private fun countKategorier(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter.forEach {
            countEvent(
                eventName = KATEGORIER_EVENT_NAME,
                result = it.kategori ?: MANGLENDE_KATEGORI_INDIKATOR
            )

            countEvent(
                eventName = UNDERKATEGORIER_EVENT_NAME,
                result = it.underkategori ?: MANGLENDE_KATEGORI_INDIKATOR
            )
        }
    }

    private fun countMaaneder(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .flatMap { it.utbetalingsperioder ?: emptyList() }
            .forEach(::countMaaneder)
    }

    private fun countPeriodeAntall(rettigheter: List<PensjonsrettighetDto>) {
        rettigheter
            .map { it.utbetalingsperioder?.size ?: 0 }
            .forEach(::countPeriodeAntall)
    }

    private fun countMaaneder(periode: UtbetalingsperiodeDto) {
        countEvent(eventName = MAANEDER_EVENT_NAME, result = "${periode.startMaaned}-${sluttMaaned(periode)}")
    }

    private fun countPeriodeAntall(antall: Int) {
        countEvent(eventName = PERIODE_ANTALL_EVENT_NAME, result = "$antall")
    }

    private fun sluttMaaned(periode: UtbetalingsperiodeDto) = periode.sluttMaaned ?: MANGLENDE_SLUTTMAANED_INDIKATOR
}
