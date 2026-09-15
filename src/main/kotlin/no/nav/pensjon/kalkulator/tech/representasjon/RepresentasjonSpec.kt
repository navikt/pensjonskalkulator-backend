package no.nav.pensjon.kalkulator.tech.representasjon

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid

/**
 * Spesifiserer hva representasjonstjenesten skal sjekke og hvilke data den skal returnere.
 */
data class RepresentasjonSpec(
    val fullmaktsgiverPid: PossiblyEncryptedPid,
    val fullmektigPid: Pid,
    val gyldigeRepresentasjonstyper: List<Representasjonstype>,
    val inkluderRepresentertNavn: Boolean
)