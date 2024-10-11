package no.nav.pensjon.kalkulator.general.alder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.PersonService
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Simulator utilities related to "normalder".
 * The term "normalder" is defined in "NOU 2022: 7 - Et forbedret pensjonssystem"
 * (https://www.regjeringen.no/no/dokumenter/nou-2022-7/id2918654/?ch=10#kap9-1):
 * "aldersgrensen for ubetinget rett til alderspensjon som i dag (2024) er 67 år,
 *  kalles 'normert pensjoneringsalder', med 'normalderen' som kortform"
 */
@Service
class NormAlderService(
    private val personService: PersonService
) {
    fun normAlder(): Alder =
        normAlder(personService.getPerson().foedselsdato)

    fun nedreAldersgrense(): Alder =
        normAlder() minusAar ANTALL_AAR_FRA_NEDRE_ALDERSGRENSE_TIL_UBETINGET_UTTAK

    private companion object {
        private const val ANTALL_AAR_FRA_NEDRE_ALDERSGRENSE_TIL_UBETINGET_UTTAK = 5

        private fun normAlder(fodselsdato: LocalDate) = Alder(aar = 67, maaneder = 0)
    }
}
