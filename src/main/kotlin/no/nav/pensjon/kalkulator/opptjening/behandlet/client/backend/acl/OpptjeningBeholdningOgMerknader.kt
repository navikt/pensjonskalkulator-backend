package no.nav.pensjon.kalkulator.opptjening.behandlet.client.backend.acl

import no.nav.pensjon.kalkulator.opptjening.behandlet.Beholdning
import no.nav.pensjon.kalkulator.opptjening.behandlet.BeholdningOgMerknader

/**
 * Korresponderer med BeholdningOgMerknaderV1 i pensjon-selvbetjening-opptjening-backend, ref.
 * github.com/navikt/pensjon-selvbetjening-opptjening-backend/.../api/opptjening/v1/acl/BeholdningOgMerknaderV1.kt
 */
data class OpptjeningBeholdningOgMerknader(
    val beholdning: OpptjeningBeholdning? = null,
    val merknadListe: List<OpptjeningMerknad>? = null
) {
    fun toInternalValue() =
        BeholdningOgMerknader(
            beholdning = beholdning?.toInternalValue() ?: Beholdning(beloep = 0),
            merknadListe = merknadListe.orEmpty().map { it.toInternalValue() }
        )
}