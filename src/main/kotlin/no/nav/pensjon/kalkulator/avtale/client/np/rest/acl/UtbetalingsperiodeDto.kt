package no.nav.pensjon.kalkulator.avtale.client.np.rest.acl

import java.time.LocalDate

class UtbetalingsperiodeDto {
    var datoFom: LocalDate? = null
    var datoTom: LocalDate? = null
    var aarligUtbetalingForventet: Int? = null
    var grad: Int = 0
}