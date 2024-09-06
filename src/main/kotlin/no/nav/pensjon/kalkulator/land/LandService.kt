package no.nav.pensjon.kalkulator.land

import org.springframework.stereotype.Service

@Service
class LandService {

    fun landListe(): List<LandInfo> =
        Land.entries
            .filter { it != Land.NOR } // only 'utland' needed
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
