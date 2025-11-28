package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.general.Alder
import java.time.LocalDate

object PersonligSimuleringResultUtil {

    fun <T> filtrerBortGjeldendeAlderFoerBursdagInnevaerendeMaaned(
        list: List<T>,
        foedselsdato: LocalDate,
        alderExtractor: (T) -> Int
    ): List<T> {
        val idag = LocalDate.now()

        val harBursdagSenereDenneMaaneden =
            foedselsdato.monthValue == idag.monthValue && foedselsdato.dayOfMonth >= idag.dayOfMonth

        if (harBursdagSenereDenneMaaneden) {
            val alderAarTilAaTaBort = Alder.from(foedselDato = foedselsdato, dato = idag).aar
            return list.filter { alderExtractor(it) != alderAarTilAaTaBort }
        }

        return list
    }
}
