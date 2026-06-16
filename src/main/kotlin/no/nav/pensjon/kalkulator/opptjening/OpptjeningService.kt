package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class OpptjeningService(
    private val client: PensjonspoengClient,
    private val pidGetter: PidGetter
) {
    fun opptjening(): List<AarligOpptjening> =
        client.fetchOpptjeningOgBeholdning(pidGetter.pid()).let {
            merge(
                opptjeningListe = it.first,
                beholdningListe = it.second
            )
        }

    private fun merge(
        opptjeningListe: List<AarligOpptjening>,
        beholdningListe: List<AarligBeholdning>
    ): List<AarligOpptjening> {
        val foersteAar = minAar(opptjeningListe).coerceAtMost(minAar(beholdningListe))
        val sisteAar = maxAar(opptjeningListe).coerceAtLeast(maxAar(beholdningListe))
        if (foersteAar > sisteAar) return emptyList()

        val liste = mutableListOf<AarligOpptjening>()

        for (aar in foersteAar..sisteAar) {
            val beholdning = beholdningListe.firstOrNull { it.aar == aar }?.beholdning ?: 0

            liste.add(
                opptjeningListe.firstOrNull { it.aar == aar }?.withBeholdning(beholdning)
                    ?: bareBeholdning(aar, beholdning)
            )
        }

        return liste
    }

    private companion object {

        private fun minAar(aarligListe: List<Aarlig>): Int =
            aarligListe.minOfOrNull { it.aar } ?: 9999

        private fun maxAar(aarligListe: List<Aarlig>): Int =
            aarligListe.maxOfOrNull { it.aar } ?: 0

        private fun bareBeholdning(aar: Int, beholdning: Int) =
            AarligOpptjening(
                aar,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning
            )
    }
}