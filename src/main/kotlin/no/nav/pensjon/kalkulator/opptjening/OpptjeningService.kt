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
    fun opptjening(): List<AarligOpptjening> {
        val pid = pidGetter.pid()

        return opptjeningClient.fetchOpptjeningOgBeholdning(pid).let {
            opptjeningMedMerknader(pid, opptjeningListe = it.first, beholdningListe = it.second)
        }
    }

    fun opptjeningMedMerknader(
        pid: Pid,
        opptjeningListe: List<AarligOpptjening>,
        beholdningListe: List<AarligBeholdning>
    ): List<AarligOpptjening> {
        val foersteAar = minAar(opptjeningListe).coerceAtMost(minAar(beholdningListe))
        val sisteAar = maxAar(opptjeningListe).coerceAtLeast(maxAar(beholdningListe))
        val opptjeningComboListe = merge(opptjeningListe, beholdningListe, foersteAar, sisteAar)

        return merge(
            opptjeningListe = opptjeningComboListe,
            merknaderPerAar = merknadClient.fetchMerknader(pid).perAar,
            foersteOpptjeningAar = foersteAar,
            sisteOppptjeningAar = sisteAar
        )
    }

    private fun merge(
        opptjeningListe: List<AarligOpptjening>,
        beholdningListe: List<AarligBeholdning>,
        foersteAar: Int,
        sisteAar: Int
    ): List<AarligOpptjening> {
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
        merknaderPerAar: Map<Int, List<MerknadCode>>,
        foersteOpptjeningAar: Int,
        sisteOppptjeningAar: Int
    ): List<AarligOpptjening> {
        val nonEmptyMerknaderPerAar = merknaderPerAar.filter { it.value.isNotEmpty() }
        val foersteMerknadAar = nonEmptyMerknaderPerAar.minOfOrNull { it.key } ?: 9999
        val sisteMerknadAar = nonEmptyMerknaderPerAar.maxOfOrNull { it.key } ?: 0
        val foersteAar = foersteOpptjeningAar.coerceAtMost(foersteMerknadAar)
        val sisteAar = sisteOppptjeningAar.coerceAtLeast(sisteMerknadAar)
        if (foersteAar > sisteAar) return emptyList()

        val liste = mutableListOf<AarligOpptjening>()

        for (aar in foersteAar..sisteAar) {
            merknaderPerAar[aar].orEmpty().let {
                liste.add(
                    opptjening(opptjeningListe, aar)?.withMerknadListe(it) ?: bareMerknader(aar, merknadListe = it)
                )
            }
        }

        return liste
    }

    private companion object {

        private fun minAar(aarligListe: List<Aarlig>): Int =
            aarligListe.minOfOrNull { it.aar } ?: 9999

        private fun maxAar(aarligListe: List<Aarlig>): Int =
            aarligListe.maxOfOrNull { it.aar } ?: 0

        private fun opptjening(opptjeningListe: List<AarligOpptjening>, aar: Int): AarligOpptjening? =
            opptjeningListe.firstOrNull { it.aar == aar }

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