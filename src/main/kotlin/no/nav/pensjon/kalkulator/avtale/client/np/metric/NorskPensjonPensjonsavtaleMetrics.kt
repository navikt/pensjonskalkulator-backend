package no.nav.pensjon.kalkulator.avtale.client.np.metric

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.tech.metric.Metrics.countEvent

object NorskPensjonPensjonsavtaleMetrics {

    private const val KATEGORIER_UTEN_UTBETALINGSPERIODER_EVENT_NAME = "avtalekategorierutenperioder"
    private const val UNDERKATEGORIER_EVENT_NAME = "avtaleunderkategorier"
    private const val MAANEDER_EVENT_NAME = "avtaleperiodemaaneder"
    private const val MANGLENDE_SLUTTMAANED_INDIKATOR = ">"
    private const val MANGLENDE_KATEGORI_INDIKATOR = "UDEFINERT"

    fun updateMetrics(envelope: Pensjonsavtaler) {
        envelope.avtaler.let(::countInterestingEvents)
    }

    private fun countInterestingEvents(rettigheter: List<Pensjonsavtale>) {
        countKategorier(rettigheter)
        countKategorierUtenUtbetalingsperioder(rettigheter)
        countMaaneder(rettigheter)
    }

    /**
     * Teller forekomster av underkategorier.
     * Utelater hovedkategorier (individuelleOrdninger, offentligTjenestepensjon, privatTjenestepensjon).
     */
    private fun countKategorier(rettigheter: List<Pensjonsavtale>) {
        rettigheter.forEach {
            countEvent(
                eventName = UNDERKATEGORIER_EVENT_NAME,
                result = justerHvisUnknown(it.underkategori.name)
            )
        }
    }

    private fun countKategorierUtenUtbetalingsperioder(rettigheter: List<Pensjonsavtale>) {
        rettigheter
            .filter { it.utbetalingsperioder.isEmpty() }
            .forEach {
                countEvent(
                    eventName = KATEGORIER_UTEN_UTBETALINGSPERIODER_EVENT_NAME,
                    result = "${justerHvisUnknown(it.kategori.name)}:${justerHvisUnknown(it.underkategori.name)}"
                )
            }
    }

    private fun countMaaneder(rettigheter: List<Pensjonsavtale>) {
        rettigheter
            .flatMap { it.utbetalingsperioder }
            .forEach(::countMaaneder)
    }

    private fun countMaaneder(periode: Utbetalingsperiode) {
        countEvent(
            eventName = MAANEDER_EVENT_NAME,
            result = "${maaned1_12(periode.startAlder.maaneder)}-${sluttMaaned(periode)}"
        )
    }

    private fun sluttMaaned(periode: Utbetalingsperiode) =
        periode.sluttAlder?.maaneder?.let(::maaned1_12) ?: MANGLENDE_SLUTTMAANED_INDIKATOR

    private fun maaned1_12(maaned: Int) = maaned + 1

    private fun justerHvisUnknown(kategori: String): String {
        return if (kategori != "UNKNOWN") kategori else MANGLENDE_KATEGORI_INDIKATOR
    }
}
