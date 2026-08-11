package no.nav.pensjon.kalkulator.afp

import no.nav.pensjon.kalkulator.opptjening.AarligOpptjening
import no.nav.pensjon.kalkulator.simulering.AfpOrdningType
import java.time.LocalDate

data class ServiceberegnetAfpResult(
    val afpOrdning: AfpOrdningType?,
    val beregnetAfp: BeregnetAfp?,
    val opptjeningListe: List<AarligOpptjening> = emptyList(),
    val problem: ServiceberegnetAfpProblem?
) {
    fun withOpptjening(opptjeningListe: List<AarligOpptjening>) =
        copy(opptjeningListe = opptjeningListe)
}

data class BeregnetAfp(
    val afpTotalbeloep: Int?,
    val virkningFom: LocalDate?,
    val tidligereArbeidsinntekt: Int?,
    val grunnbeloep: Int?,
    val sluttpoengtall: Double?,
    val trygdetid: Int?,
    val poengaar: Int?,
    val poengaarFoer1992: Int?,
    val poengaarEtter1991: Int?,
    val grunnpensjon: Int?,
    val tilleggspensjon: Int?,
    val afpTillegg: Int?,
    val fpp: Double?,
    val saertillegg: Int?,
    val grad: Int?,
    val erAvkortet: Boolean?
)

data class ServiceberegnetAfpProblem(
    val type: ServiceberegnetAfpProblemType,
    val beskrivelse: String
)

enum class ServiceberegnetAfpProblemType {
    UTILSTREKKELIG_TRYGDETID,
    UTILSTREKKELIG_OPPTJENING,
    ANNEN_KLIENTFEIL
}