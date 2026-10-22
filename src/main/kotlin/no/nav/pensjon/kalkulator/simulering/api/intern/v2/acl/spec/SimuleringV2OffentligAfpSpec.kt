package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.spec

import io.swagger.v3.oas.annotations.media.Schema

data class SimuleringV2OffentligAfpSpec(
    @field:Schema(description = "Hvorvidt personen har inntekt måneden før uttaket starter")
    val harInntektMaanedenFoerUttak: Boolean? = null,

    @field:Schema(description = "Type AFP-ordning personen er tilknyttet")
    val afpOrdning: SimuleringV2AfpOrdningTypeSpec? = null,

    @field:Schema(description = "Liste over innvilget livsvarig AFP i offentlig sektor")
    val innvilgetLivsvarigAfpListe: List<SimuleringV2InnvilgetLivsvarigOffentligAfpSpec>? = null,

    // SERVICEBEREGN_AFP-specific fields (required when simuleringstype = SERVICEBEREGN_AFP, ignored otherwise)
    @field:Schema(description = "Årlig inntekt i forrige kalenderår (beløp i norske kroner)")
    val inntektForrigeKalenderaar: Int? = null,

    @field:Schema(description = "Årlig inntekt frem til start av uttak (beløp i norske kroner)")
    val inntektFremTilUttak: Int? = null,

    @field:Schema(description = "Årlig inntekt måneden før start av AFP (beløp i norske kroner)")
    val inntektMaanedFoerAfp: Int? = null
)