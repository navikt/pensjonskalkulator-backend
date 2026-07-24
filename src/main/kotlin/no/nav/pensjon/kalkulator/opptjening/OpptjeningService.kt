package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig
import no.nav.pensjon.kalkulator.merknad.MerknadCode
import no.nav.pensjon.kalkulator.merknad.client.MerknadClient
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service
import kotlin.collections.emptyList

@Service
class OpptjeningService(
    private val opptjeningClient: PensjonspoengClient,
    private val merknadClient: MerknadClient,
    private val pidGetter: PidGetter
) {
    fun opptjening(): List<AarligOpptjening> =
        pidGetter.pid().let {
            merge(
                opptjeningListe = opptjeningComboListe(pid = it),
                merknaderPerAar = merknadClient.fetchMerknader(pid = it).perAar
            )
        }

    private fun opptjeningComboListe(pid: Pid): List<AarligOpptjening> =
        opptjeningClient.fetchOpptjeningOgBeholdning(pid).let {
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

    private fun merge(
        opptjeningListe: List<AarligOpptjening>,
        merknaderPerAar: Map<Int, List<MerknadCode>>
    ): List<AarligOpptjening> {
        val merknaderPerAarListe: List<Pair<Int, List<MerknadCode>>> = merknaderPerAar.toList()
        val foersteAar = minAar(opptjeningListe).coerceAtMost(minMerknadAar(merknaderPerAarListe))
        val sisteAar = maxAar(opptjeningListe).coerceAtLeast(maxMerknadAar(merknaderPerAarListe))
        if (foersteAar > sisteAar) return emptyList()

        val liste = mutableListOf<AarligOpptjening>()

        for (aar in foersteAar..sisteAar) {
            val merknadListe: List<MerknadCode> =
                merknaderPerAarListe.firstOrNull { it.first == aar }?.second ?: emptyList()

            liste.add(
                opptjeningListe.firstOrNull { it.aar == aar }?.withMerknadListe(merknadListe)
                    ?: bareMerknader(aar, merknadListe)
            )
        }

        return liste
    }

    private companion object {

        private fun minAar(aarligListe: List<Aarlig>): Int =
            aarligListe.minOfOrNull { it.aar } ?: 9999

        private fun minMerknadAar(aarligListe: List<Pair<Int, List<MerknadCode>>>): Int =
            aarligListe.minOfOrNull { it.first } ?: 9999

        private fun maxAar(aarligListe: List<Aarlig>): Int =
            aarligListe.maxOfOrNull { it.aar } ?: 0

        private fun maxMerknadAar(aarligListe: List<Pair<Int, List<MerknadCode>>>): Int =
            aarligListe.maxOfOrNull { it.first } ?: 0

        private fun bareBeholdning(aar: Int, beholdning: Int) =
            AarligOpptjening(
                aar,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning,
                merknadListe = emptyList()
            )

        private fun bareMerknader(aar: Int, merknadListe: List<MerknadCode>) =
            AarligOpptjening(
                aar,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                omsorgspoeng = 0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = "",
                beholdning = 0,
                merknadListe
            )
    }
}