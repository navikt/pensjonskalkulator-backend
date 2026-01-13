package no.nav.pensjon.kalkulator.normalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.client.NormertPensjonsalderClient
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Simulator utilities related to "normalder".
 * The term "normalder" is defined in "NOU 2022: 7 - Et forbedret pensjonssystem"
 * (https://www.regjeringen.no/no/dokumenter/nou-2022-7/id2918654/?ch=10#kap9-1):
 * "aldersgrensen for ubetinget rett til alderspensjon som i dag (2024) er 67 år,
 *  kalles 'normert pensjoneringsalder', med 'normalderen' som kortform".
 * I Nav brukes begrepet 'normert pensjonsalder'.
 */
@Service
class NormertPensjonsalderService(private val normalderClient: NormertPensjonsalderClient) {

    fun aldersgrenser(spec: AldersgrenseSpec): Aldersgrenser =
        aldersgrenser(
            foedselsdato = LocalDate.of(spec.aarskull, 1, 1)
        )

    fun aldersgrenser(foedselsdato: LocalDate): Aldersgrenser =
        if (System.getenv("NAIS_CLUSTER_NAME") == "dev-gcp")
            testAldersgrenser(foedselsdato)
        else
            normalderClient.fetchNormalderListe().first { it.aarskull == foedselsdato.year }

    fun nedreAlder(foedselsdato: LocalDate): Alder =
        aldersgrenser(foedselsdato).nedreAlder

    fun normalder(foedselsdato: LocalDate): Alder =
        aldersgrenser(foedselsdato).normalder

    /**
     * Temporary function to test 'økte aldersgrenser'.
     */
    private fun testAldersgrenser(foedselsdato: LocalDate): Aldersgrenser =
        when (foedselsdato) {
            LocalDate.of(1969, 6, 11) -> Aldersgrenser(
                aarskull = 1969,
                nedreAlder = Alder(62, 4),
                normalder = Alder(67, 4),
                oevreAlder = Alder(75, 4),
                verdiStatus = VerdiStatus.PROGNOSE
            )

            LocalDate.of(1974, 3, 21) -> Aldersgrenser(
                aarskull = 1974,
                nedreAlder = Alder(63, 0),
                normalder = Alder(68, 0),
                oevreAlder = Alder(76, 0),
                verdiStatus = VerdiStatus.PROGNOSE
            )

            else -> normalderClient.fetchNormalderListe().first { it.aarskull == foedselsdato.year }
        }

    companion object {
        val defaultAldersgrenser =
            Aldersgrenser(
                aarskull = 1900, // don't care
                nedreAlder = Alder(62, 0),
                normalder = Alder(67, 0),
                oevreAlder = Alder(75, 0),
                verdiStatus = VerdiStatus.FAST // don't care
            )
    }
}
