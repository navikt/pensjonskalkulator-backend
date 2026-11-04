package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.opptjening.behandlet.BeholdningOgMerknadService
import no.nav.pensjon.kalkulator.opptjening.behandlet.BeholdningOgMerknader
import no.nav.pensjon.kalkulator.opptjening.client.PensjonspoengClient
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class OpptjeningService(
    private val poengClient: PensjonspoengClient,
    private val beholdningOgMerknadService: BeholdningOgMerknadService,
    private val pidGetter: PidGetter
) {
    fun opptjening(): List<AarligOpptjening> =
        merge(
            poengListe = poengClient.fetchPensjonspoeng(pidGetter.pid()),
            beholdningOgMerknaderPerAar = beholdningOgMerknadService.beholdningOgMerknader().perAar
        )

    private fun merge(
        poengListe: List<AarligOpptjening>,
        beholdningOgMerknaderPerAar: Map<Int, BeholdningOgMerknader>
    ): List<AarligOpptjening> {
        val relevantePerAar = beholdningOgMerknaderPerAar.filter { it.value.erRelevant() }
        val foersteAar = minAar(poengListe).coerceAtMost(minAar(relevantePerAar))
        val sisteAar = maxAar(poengListe).coerceAtLeast(maxAar(relevantePerAar))

        return if (foersteAar > sisteAar)
            emptyList()
        else
            IntRange(foersteAar, sisteAar)
                .map {
                    merge(
                        aar = it,
                        poeng = pensjonspoeng(poengListe, aar = it),
                        beholdningOgMerknader = beholdningOgMerknaderPerAar[it]
                    )
                }
    }

    private fun merge(
        aar: Int,
        poeng: AarligOpptjening?,
        beholdningOgMerknader: BeholdningOgMerknader?
    ): AarligOpptjening =
        poeng?.medBeholdningOgMerknader(
            beholdningBeloep = beholdningOgMerknader?.beholdning?.beloep ?: 0,
            merknadListe = beholdningOgMerknader?.merknadListe.orEmpty()
        ) ?: kunBeholdningOgMerknader(aar, beholdningOgMerknader)

    private companion object {

        private fun kunBeholdningOgMerknader(aar: Int, beholdningOgMerknader: BeholdningOgMerknader?) =
            AarligOpptjening(
                aar = aar,
                pensjonsgivendeInntekt = 0,
                pensjonspoeng = 0.0,
                maksimalUfoeregrad = null,
                pensjonspoengType = PensjonspoengType.UNKNOWN,
                beholdning = beholdningOgMerknader?.beholdning?.beloep ?: 0,
                merknadListe = beholdningOgMerknader?.merknadListe.orEmpty()
            )

        private fun pensjonspoeng(poengListe: List<AarligOpptjening>, aar: Int): AarligOpptjening? =
            poengListe.firstOrNull { it.aar == aar }

        private fun minAar(aarligListe: List<AarligOpptjening>): Int =
            aarligListe.minOfOrNull { it.aar } ?: 9999

        private fun maxAar(aarligListe: List<AarligOpptjening>): Int =
            aarligListe.maxOfOrNull { it.aar } ?: 0

        private fun maxAar(perAar: Map<Int, BeholdningOgMerknader>): Int =
            perAar.maxOfOrNull { it.key } ?: 0

        private fun minAar(perAar: Map<Int, BeholdningOgMerknader>): Int =
            perAar.minOfOrNull { it.key } ?: 9999
    }
}