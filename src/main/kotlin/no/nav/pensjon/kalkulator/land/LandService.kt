package no.nav.pensjon.kalkulator.land

import org.springframework.stereotype.Service

@Service
class LandService {

    fun landListe() = landListe

    private companion object {

        private val landListe: List<LandInfo> =
            Land.entries
                .filter { it.erEkskludert.not() && it.erHistorisk.not() }
                .map {
                    LandInfo(
                        landkode = it.name,
                        kravOmArbeid = it.kravOmArbeid,
                        bokmaalNavn = it.bokmaalNavn,
                        nynorskNavn = it.bokmaalNavn,
                        engelskNavn = it.engelskNavn
                    )
                }
    }
}
