package no.nav.pensjon.kalkulator.simulering

import java.time.LocalDate

object PensjonUtil {

    private const val MAANEDER_PER_AAR = 12

    /**
     * Pensjonsår = fødselsår + pensjonsalder + 1 måned
     */
    fun pensjonsaar(foedselsdato: LocalDate, pensjonsalder: Int): Int =
        foedselsdato.year + pensjonsalder + foedselsdato.monthValue / MAANEDER_PER_AAR
}
