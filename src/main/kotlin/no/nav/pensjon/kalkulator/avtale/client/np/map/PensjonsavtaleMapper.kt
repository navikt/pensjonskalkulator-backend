package no.nav.pensjon.kalkulator.avtale.client.np.map

import no.nav.pensjon.kalkulator.avtale.Alder
import no.nav.pensjon.kalkulator.avtale.Pensjonsavtale
import no.nav.pensjon.kalkulator.avtale.Utbetalingsperiode
import no.nav.pensjon.kalkulator.avtale.client.np.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.UtbetalingsperioderDto

object PensjonsavtaleMapper {

    fun fromDto(dto: EnvelopeDto): Pensjonsavtale {
        val source = dto.body!!.privatPensjonsrettigheter!!.privatAlderRettigheterDto!!

        return Pensjonsavtale(
            source.produktbetegnelse ?: "ukjent",
            source.kategori ?: "ukjent",
            source.startAlder ?: 0,
            source.sluttAlder,
            utbetalingsperiode(source.utbetalingsperioder!!)
        )
    }

    private fun utbetalingsperiode(source: UtbetalingsperioderDto) =
        Utbetalingsperiode(
            source.startAlder!!.let { Alder(it, source.startMaaned!!) },
            source.sluttAlder?.let { Alder(it, source.sluttMaaned!!) },
            source.aarligUtbetaling ?: 0,
            source.grad ?: 0
        )
}
