package no.nav.pensjon.kalkulator.tech.representasjon.client.pensjon.dto

import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonSpec

/**
 * Corresponds with ValidRepresentasjonsforholdRequest in pensjon-representasjon, ref.
 * github.com/navikt/pensjon-representasjon/src/main/kotlin/no/nav/pensjon/fullmakt/representasjon/RepresentasjonsforholdController
 */
data class PensjonRepresentasjonSpec(
    val representertPid: String,
    val representantPid: String,
    val validRepresentasjonstyper: List<String>,
    val includeRepresentertNavn: Boolean
) {
    companion object {
        fun from(source: RepresentasjonSpec) =
            PensjonRepresentasjonSpec(
                representertPid = source.fullmaktsgiverPid.value,
                representantPid = source.fullmektigPid.value,
                validRepresentasjonstyper = source.gyldigeRepresentasjonstyper
                    .map(PensjonRepresentasjonstype::transferable),
                includeRepresentertNavn = source.inkluderRepresentertNavn
            )
    }
}