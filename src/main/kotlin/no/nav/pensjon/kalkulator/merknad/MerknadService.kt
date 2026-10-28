package no.nav.pensjon.kalkulator.merknad

import no.nav.pensjon.kalkulator.merknad.client.MerknadClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class MerknadService(
    private val client: MerknadClient,
    private val pidGetter: PidGetter
) {
    fun merknader() =
        Merknader(
            perAar = client.fetchMerknader(pidGetter.pid()).perAar
                .filter { it.value.isNotEmpty() }
                .map { (aar, merknader) -> aar to merknader.filterNot { it.kode == MerknadCode.INGEN_OPPTJENING } }
                .toMap()
        )
}