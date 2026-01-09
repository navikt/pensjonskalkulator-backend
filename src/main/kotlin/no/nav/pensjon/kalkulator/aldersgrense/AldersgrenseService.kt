package no.nav.pensjon.kalkulator.aldersgrense

import no.nav.pensjon.kalkulator.normalder.AldersgrenseSpec
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AldersgrenseService(private val normalderService: NormertPensjonsalderService) {

    fun hentAldersgrenser(spec: AldersgrenseSpec): Aldersgrenser =
        normalderService.aldersgrenser(
            foedselsdato = LocalDate.of(spec.aarskull, 1, 1)
        )
}
