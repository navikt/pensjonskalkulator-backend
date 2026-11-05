package no.nav.pensjon.kalkulator.opptjening

import no.nav.pensjon.kalkulator.opptjening.behandlet.BeholdningOgMerknader
import no.nav.pensjon.kalkulator.simulering.SimulertOpptjening

/**
 * Slår sammen årlige simulerte opptjeningsdata og årlige reelle 'beholdning og merknader'-data.
 */
object OpptjeningMerger {

    fun merge(
        opptjeningListe: List<SimulertOpptjening>,
        beholdningOgMerknaderPerAar: Map<Int, BeholdningOgMerknader>
    ): List<SimulertOpptjening> {
        val relevantePerAar = beholdningOgMerknaderPerAar.filter { it.value.erRelevant() }
        val foersteAar = minAar(opptjeningListe).coerceAtMost(minAar(relevantePerAar))
        val sisteAar = maxAar(opptjeningListe).coerceAtLeast(maxAar(relevantePerAar))

        return if (foersteAar > sisteAar)
            emptyList()
        else
            (foersteAar..sisteAar).map {
                merge(
                    aar = it,
                    opptjening = opptjening(opptjeningListe, aar = it),
                    beholdningOgMerknader = relevantePerAar[it]
                )
            }
    }

    /**
     * Overskriver beholdninger fra simulatoren med beholdninger fra opptjening-backend.
     * Dette fordi sistnevnte gir riktigere beholdningsverdier ved pensjonsuttak.
     * Hvis simulatoren gir beholdningsverdier for år der de mangler fra opptjening-backend,
     * så brukes verdiene fra simulatoren for disse årene.
     */
    private fun merge(
        aar: Int,
        opptjening: SimulertOpptjening?,
        beholdningOgMerknader: BeholdningOgMerknader?
    ): SimulertOpptjening =
        opptjening?.medBeholdningOgMerknader(
            beholdningBeloep = beholdningOgMerknader?.beholdning?.beloep ?: opptjening.pensjonsbeholdningBeloep,
            merknadListe = beholdningOgMerknader?.merknadListe ?: opptjening.merknadListe
        ) ?: kunBeholdningOgMerknader(aar, beholdningOgMerknader)

    private fun opptjening(opptjeningListe: List<SimulertOpptjening>, aar: Int): SimulertOpptjening? =
        opptjeningListe.firstOrNull { it.aarstall == aar }

    private fun minAar(aarligListe: List<SimulertOpptjening>): Int =
        aarligListe.minOfOrNull { it.aarstall } ?: 9999

    private fun maxAar(aarligListe: List<SimulertOpptjening>): Int =
        aarligListe.maxOfOrNull { it.aarstall } ?: 0

    private fun minAar(perAar: Map<Int, BeholdningOgMerknader>): Int =
        perAar.minOfOrNull { it.key } ?: 9999

    private fun maxAar(perAar: Map<Int, BeholdningOgMerknader>): Int =
        perAar.maxOfOrNull { it.key } ?: 0

    private fun kunBeholdningOgMerknader(aar: Int, beholdningOgMerknader: BeholdningOgMerknader?) =
        SimulertOpptjening(
            aarstall = aar,
            pensjonsgivendeInntektBeloep = 0,
            pensjonspoeng = 0.0,
            pensjonsbeholdningBeloep = beholdningOgMerknader?.beholdning?.beloep ?: 0,
            merknadListe = beholdningOgMerknader?.merknadListe.orEmpty()
        )
}