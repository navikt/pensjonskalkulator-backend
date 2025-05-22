package no.nav.pensjon.kalkulator.normalder

import no.nav.pensjon.kalkulator.general.Alder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Deprecated("Use NormertPensjonsalderService")
@Service
class NormertPensjoneringsalderService {
    //TODO use PEN service
    fun getNormalder(foedselsdato: LocalDate) = defaultNormalder

    fun getAldre(foedselsdato: LocalDate) = aldre(getNormalder(foedselsdato))

    companion object {
        private const val ANTALL_AAR_FRA_NEDRE_ALDERSGRENSE_TIL_NORMALDER = 5
        val defaultNormalder = Alder(aar = 67, maaneder = 0)

        val defaultAldre = aldre(defaultNormalder)

        private fun aldre(normalder: Alder) = PensjoneringAldre(
            normalder,
            nedreAldersgrense = Alder(
                aar = normalder.aar - ANTALL_AAR_FRA_NEDRE_ALDERSGRENSE_TIL_NORMALDER,
                maaneder = normalder.maaneder
            )
        )
    }
}

data class PensjoneringAldre(
    val normalder: Alder,
    val nedreAldersgrense: Alder
)
