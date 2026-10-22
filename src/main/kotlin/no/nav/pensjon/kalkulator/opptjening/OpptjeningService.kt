package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.general.Aarlig
import no.nav.pensjon.kalkulator.merknad.Merknad
import no.nav.pensjon.kalkulator.merknad.client.MerknadClient
import no.nav.pensjon.kalkulator.opptjening.BeholdningVelger.velg
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengOgBeholdningClient
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class OpptjeningService(
    private val opptjeningClient: PensjonspoengOgBeholdningClient,
    private val merknadClient: MerknadClient,
    private val pidGetter: PidGetter
) {
    fun opptjening(): List<AarligOpptjening> {
        val pid = pidGetter.pid()

        return opptjeningClient.fetchPensjonspoengOgBeholdning(pid).let {
            opptjeningMedMerknader(pid, opptjeningListe = it.first, beholdningListe = it.second)
        }
    }

    fun opptjeningMedMerknader(
        pid: Pid,
        opptjeningListe: List<AarligOpptjening>,
        beholdningListe: List<DatertBeholdning>
    ): List<AarligOpptjening> {
        val opptjeningListePerAar: Map<Int, List<AarligOpptjening>> = opptjeningListe.groupBy { it.aar }
        val beholdningListePerAar: Map<Int, List<DatertBeholdning>> = beholdningListe.groupBy { it.aar }
        val foersteComboAar = minAar(opptjeningListePerAar).coerceAtMost(minAar(beholdningListePerAar))
        val sisteComboAar = maxAar(opptjeningListePerAar).coerceAtLeast(maxAar(beholdningListePerAar))
        val opptjeningComboPerAar = merge(opptjeningListePerAar, beholdningListePerAar, foersteComboAar, sisteComboAar)
        val nonEmptyMerknaderPerAar = merknadClient.fetchMerknader(pid).perAar.filter { it.value.isNotEmpty() }
        val foersteMerknadAar = nonEmptyMerknaderPerAar.minOfOrNull { it.key } ?: foersteComboAar
        val sisteMerknadAar = nonEmptyMerknaderPerAar.maxOfOrNull { it.key } ?: sisteComboAar

        return merge(
            opptjeningPerAar = opptjeningComboPerAar,
            merknadListePerAar = nonEmptyMerknaderPerAar,
            foersteAar = foersteComboAar.coerceAtMost(foersteMerknadAar),
            sisteAar = sisteComboAar.coerceAtLeast(sisteMerknadAar)
        )
    }

    private fun merge(
        opptjeningListePerAar: Map<Int, List<AarligOpptjening>>,
        beholdningListePerAar: Map<Int, List<DatertBeholdning>>,
        foersteAar: Int,
        sisteAar: Int
    ): Map<Int, AarligOpptjening> =
        if (foersteAar > sisteAar)
            emptyMap()
        else
            (foersteAar..sisteAar).associateWith {
                opptjeningCombo(
                    aar = it,
                    opptjening = opptjeningListePerAar[it]?.firstOrNull(),
                    beholdning = beholdningListePerAar[it]?.let(::velg)?.beholdning ?: 0
                )
            }

    private fun merge(
        opptjeningPerAar: Map<Int, AarligOpptjening>,
        merknadListePerAar: Map<Int, List<Merknad>>,
        foersteAar: Int,
        sisteAar: Int
    ): List<AarligOpptjening> =
         if (foersteAar > sisteAar)
            emptyList()
        else
            (foersteAar..sisteAar).map {
                opptjeningCombo(
                    aar = it,
                    opptjening = opptjeningPerAar[it],
                    merknadListe = merknadListePerAar[it].orEmpty()
                )
            }

    private companion object {

        private fun minAar(map: Map<Int, List<Aarlig>>): Int =
            map.minOfOrNull { it.key } ?: 9999

        private fun maxAar(map: Map<Int, List<Aarlig>>): Int =
            map.maxOfOrNull { it.key } ?: 0

        private fun opptjeningCombo(
            aar: Int,
            opptjening: AarligOpptjening?,
            beholdning: Int
        ): AarligOpptjening =
            opptjening?.withBeholdning(beholdning) ?: kunBeholdning(aar, beholdning)

        private fun opptjeningCombo(
            aar: Int,
            opptjening: AarligOpptjening?,
            merknadListe: List<Merknad>
        ): AarligOpptjening =
            opptjening?.withMerknadListe(merknadListe) ?: kunMerknadListe(aar, merknadListe)

        private fun kunBeholdning(aar: Int, beholdning: Int) =
            AarligOpptjening(
                aar,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning,
                merknadListe = emptyList()
            )

        private fun kunMerknadListe(aar: Int, merknadListe: List<Merknad>) =
            AarligOpptjening(
                aar,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = 0,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = 0,
                merknadListe
            )
    }
}