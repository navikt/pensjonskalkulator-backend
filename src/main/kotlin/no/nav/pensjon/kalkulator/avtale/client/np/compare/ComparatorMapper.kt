package no.nav.pensjon.kalkulator.avtale.client.np.compare

import no.nav.pensjon.kalkulator.avtale.Pensjonsavtaler

object ComparatorMapper {

    fun mapToForenklet(pensjonsavtaler: Pensjonsavtaler): List<ForenkletPensjonsavtale> {
        return pensjonsavtaler.avtaler.map {
            ForenkletPensjonsavtale(
                startAar = it.startAar,
                sluttAar = it.sluttAar,
                utbetalingsperioder = it.utbetalingsperioder.map { utbetalingsperiode ->
                    ForenkletUtbetalingsperiode(
                        aarligUtbetalingForventet = utbetalingsperiode.aarligUtbetalingForventet,
                        startAlder = utbetalingsperiode.startAlder,
                        sluttAlder = utbetalingsperiode.sluttAlder
                    )
                }
            )
        }
    }
}