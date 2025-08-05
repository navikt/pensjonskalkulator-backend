package no.nav.pensjon.kalkulator.aldersgrense

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseSpec
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.NormertPensjonsalderService
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AldersgrenseService(
    private val normertPensjonsalderService: NormertPensjonsalderService
) {
    fun hentAldersgrenser(spec: AldersgrenseSpec): Aldersgrenser {
        if (System.getenv("NAIS_CLUSTER_NAME") == "dev-gcp") {
            return mockedAldersgrenser()
        }

        val foedselsdato = LocalDate.of(spec.foedselsdato, 1, 1)
        val aldre = normertPensjonsalderService.aldersgrenser(foedselsdato)
        return aldre
    }

    private fun mockedAldersgrenser(): Aldersgrenser {
        return Aldersgrenser(
            aarskull = 1963,
            normalder = Alder(aar = 67, maaneder = 5),
            nedreAlder = Alder(aar = 62, maaneder = 5),
            oevreAlder = Alder(aar = 75, maaneder = 5),
            verdiStatus = VerdiStatus.FAST
        )
    }
}
