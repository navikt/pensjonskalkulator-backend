package no.nav.pensjon.kalkulator.simulering

import java.math.BigDecimal

/**
 * Minimised variant of
 * https://github.com/navikt/pesys/blob/main/pen/domain/nav-domain-pensjon-pen-java/src/main/java/no/nav/domain/pensjon/kjerne/simulering/Pensjonsperiode.java
 */
data class Pensjonsperiode(val alder: Int, val pensjonsbeloep: BigDecimal)
