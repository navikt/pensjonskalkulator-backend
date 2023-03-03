package no.nav.pensjon.kalkulator.opptjening.client

import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import java.time.LocalDate

data class OpptjeningshistorikkSpec(
    val opptjeninger: List<OpptjeningSpec>,
    val foedselsdato: LocalDate
)

data class OpptjeningSpec(
    val aar: Int,
    val pensjonsgivendeInntekt: Int,
    val opptjeningstype: Opptjeningstype
)
