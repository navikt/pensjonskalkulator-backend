package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    private val client: PensjonsavtaleClient,
) {
    fun fetchAvtaler(spec: PensjonsavtaleSpec) = filter(client.fetchAvtaler(spec))

    private companion object {

        private fun filter(avtaler: Pensjonsavtaler) =
            Pensjonsavtaler(
                avtaler = avtaler.avtaler.filter { it.kategori.included && it.harStartAar },
                utilgjengeligeSelskap = avtaler.utilgjengeligeSelskap
            )
    }
}
