package no.nav.pensjon.kalkulator.tech.representasjon

import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid
import no.nav.pensjon.kalkulator.tech.representasjon.client.RepresentasjonClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class RepresentasjonService(
    private val client: RepresentasjonClient,
    private val pidGetter: PidGetter
) {
    fun hasValidRepresentasjonsforhold(fullmaktsgiverPid: PossiblyEncryptedPid): Representasjon =
        client.fetchRepresentasjon(
            spec = RepresentasjonSpec(
                fullmaktsgiverPid = fullmaktsgiverPid,
                fullmektigPid = pidGetter.pid(),
                gyldigeRepresentasjonstyper = representasjonstyper,
                inkluderRepresentertNavn = false
            )
        )

    private companion object {
        private val representasjonstyper: List<Representasjonstype> =
            listOf(
                Representasjonstype.PENSJON_LES,
                Representasjonstype.PENSJON_SKRIV
            )
    }
}