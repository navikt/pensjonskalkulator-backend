package no.nav.pensjon.kalkulator.aldersgrense.api

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseSpec
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV4
import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AldersgrenseService(
    private val normertPensjoneringsalderService: NormertPensjoneringsalderService
) {
    fun hentAldersgrenser(spec: AldersgrenseSpec): PersonPensjoneringAldreV4 {
        val foedselsdato = LocalDate.of(spec.foedselsdato, 1, 1)
        val aldre = normertPensjoneringsalderService.getAldre(foedselsdato)
        return PersonPensjoneringAldreV4(
            normertPensjoneringsalder = aldre.normalder.toPersonAlderV4(),
            nedreAldersgrense = aldre.nedreAldersgrense.toPersonAlderV4()
        )
    }

    private fun Alder.toPersonAlderV4(): PersonAlderV4 = PersonAlderV4(aar, maaneder)
}
