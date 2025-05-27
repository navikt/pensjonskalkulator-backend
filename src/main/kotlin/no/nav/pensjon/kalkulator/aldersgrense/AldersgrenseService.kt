package no.nav.pensjon.kalkulator.aldersgrense

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseSpec
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AldersgrenseService(
    private val normertPensjonsalderService: NormertPensjonsalderService
) {
    fun hentAldersgrenser(spec: AldersgrenseSpec): Aldersgrenser {
        val foedselsdato = LocalDate.of(spec.foedselsdato, 1, 1)
        val aldre = normertPensjonsalderService.aldersgrenser(foedselsdato)
        return aldre
    }
}
