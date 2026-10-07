package no.nav.pensjon.kalkulator.avtale.client.np.compare

data class ForenkletPensjonsavtale(
    val startAar: Int, // år som i alder (antall fylte år etter fødselsdato)
    val sluttAar: Int?, // år som i alder
    val utbetalingsperioder: List<ForenkletUtbetalingsperiode>
)
