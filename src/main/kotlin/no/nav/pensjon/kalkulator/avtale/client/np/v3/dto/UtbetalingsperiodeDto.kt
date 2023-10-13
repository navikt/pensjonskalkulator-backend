package no.nav.pensjon.kalkulator.avtale.client.np.v3.dto

class UtbetalingsperiodeDto {
    var startAlder: Int = 0
    var startMaaned: Int = 0
    var sluttAlder: Int? = null
    var sluttMaaned: Int? = null
    var aarligUtbetalingForventet: Int? = null
    var aarligUtbetalingNedreGrense: Int? = null
    var aarligUtbetalingOvreGrense: Int? = null
    var grad: Int = 0
}
