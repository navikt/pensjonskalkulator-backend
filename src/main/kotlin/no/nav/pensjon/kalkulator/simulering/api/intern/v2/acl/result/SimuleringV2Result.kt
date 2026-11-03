package no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result.SimuleringResultAdjuster.justerAlderspensjonListe
import no.nav.pensjon.kalkulator.simulering.api.intern.v2.acl.result.SimuleringResultAdjuster.justerPrivatAfpListe

@JsonInclude(NON_NULL)
data class SimuleringV2Result(
    @field:Schema(description = "Alderspensjon for hvert år")
    @field:NotNull
    val alderspensjonListe: List<SimuleringV2Alderspensjon>,

    @field:Schema(description = "Månedlig alderspensjon ved endring av uttaksgrad")
    val maanedligAlderspensjonVedUttaksendring: SimuleringV2Uttaksbeloep?,

    @field:Schema(description = "Månedlig alderspensjon for knekkpunkter")
    val maanedligAlderspensjonForKnekkpunkter: SimuleringV2MaanedligAlderspensjonForKnekkpunkter?,

    @field:Schema(description = "Livsvarig AFP i offentlig sektor for hvert år")
    val livsvarigOffentligAfpListe: List<SimuleringV2AldersbestemtUtbetaling>?,

    @field:Schema(description = "Tidsbegrenset AFP i offentlig sektor ('gammel ordning') for hvert år")
    val tidsbegrensetOffentligAfp: SimuleringV2TidsbegrensetOffentligAfp?,

    @field:Schema(description = "Resultat av serviceberegnet AFP (kun for simuleringstype SERVICEBEREGN_AFP)")
    val serviceberegnetAfp: SimuleringV2ServiceberegnetAfp?,

    @field:Schema(description = "AFP i privat sektor for hvert år")
    val privatAfpListe: List<SimuleringV2PrivatAfp>?,

    @field:Schema(description = "Resultatet av vilkårsprøvingen")
    @field:NotNull
    val vilkaarsproevingsresultat: SimuleringV2Vilkaarsproevingsresultat,

    @field:Schema(description = "Personens trygdetid")
    val trygdetid: SimuleringV2Trygdetid?,

    @field:Schema(description = "Opptjeningsdata brukt i beregningen for hvert år")
    @field:NotNull
    val opptjeningListe: List<SimuleringV2Opptjening>,

    @field:Schema(description = "Eventuelt problem som oppstod under simuleringen")
    val problem: SimuleringV2Problem?
) {
    companion object {
        fun transferable(source: SimuleringResult, naavaerendeAlderAar: Int) =
            SimuleringV2Result(
                alderspensjonListe = source.alderspensjonListe.map(SimuleringV2Alderspensjon::transferable)
                    .let { justerAlderspensjonListe(pensjonListe = it, naavaerendeAlderAar) },
                maanedligAlderspensjonVedUttaksendring = source.alderspensjonMaanedsbeloep?.let(SimuleringV2Uttaksbeloep::transferable),
                maanedligAlderspensjonForKnekkpunkter = source.maanedligAlderspensjonForKnekkpunkter?.let(
                    SimuleringV2MaanedligAlderspensjonForKnekkpunkter::transferable
                ),
                livsvarigOffentligAfpListe = source.livsvarigOffentligAfpListe.map(SimuleringV2AldersbestemtUtbetaling::transferable),
                tidsbegrensetOffentligAfp = source.tidsbegrensetOffentligAfp?.let(SimuleringV2TidsbegrensetOffentligAfp::transferable),
                serviceberegnetAfp = source.serviceberegnetAfp?.let(SimuleringV2ServiceberegnetAfp::transferable),
                privatAfpListe = source.privatAfpListe.map(SimuleringV2PrivatAfp::transferable)
                    .let { justerPrivatAfpListe(pensjonListe = it, naavaerendeAlderAar) },
                vilkaarsproevingsresultat = SimuleringV2Vilkaarsproevingsresultat.transferable(source.vilkaarsproeving),
                trygdetid = SimuleringV2Trygdetid.transferable(source),
                opptjeningListe = source.opptjeningListe.map(SimuleringV2Opptjening::transferable),
                problem = source.problem?.let(SimuleringV2Problem::transferable)
            )

        fun serverfeil(e: Exception) =
            SimuleringV2Result(
                alderspensjonListe = emptyList(),
                maanedligAlderspensjonVedUttaksendring = null,
                maanedligAlderspensjonForKnekkpunkter = null,
                livsvarigOffentligAfpListe = null,
                tidsbegrensetOffentligAfp = null,
                serviceberegnetAfp = null,
                privatAfpListe = null,
                vilkaarsproevingsresultat = SimuleringV2Vilkaarsproevingsresultat.serverfeil(),
                trygdetid = null,
                opptjeningListe = emptyList(),
                problem = SimuleringV2Problem.serverfeil(e)
            )
    }
}