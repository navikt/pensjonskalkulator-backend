package no.nav.pensjon.kalkulator.avtale.client.np.compare

import no.nav.pensjon.kalkulator.general.Alder

data class ForenkletUtbetalingsperiode(
    val aarligUtbetalingForventet: Int,
    val startAlder: Alder,
    val sluttAlder: Alder?,
)
