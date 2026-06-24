package no.nav.pensjon.kalkulator.simulering.client.simulator.map

import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.simulator.dto.*

object SimulatorAnonymSimuleringResultMapper {

    fun fromDto(dto: SimulatorAnonymSimuleringResultEnvelope): SimuleringResult =
        dto.result?.let(::result)
            ?: dto.error?.let { throw SimuleringException(error = error(it)) }
            ?: throw SimuleringException("neither result nor error")

    private fun result(dto: SimulatorAnonymSimuleringResult) =
        SimuleringResult(
            alderspensjonListe = dto.alderspensjonPerioder.map(::alderspensjon),
            alderspensjonMaanedsbeloep = null, // ikke relevant i anonym kontekst
            maanedligAlderspensjonForKnekkpunkter = null, // ditto
            livsvarigOffentligAfpListe = dto.afpOffentligPerioder.map(::livsvarigOffentligAfp),
            tidsbegrensetOffentligAfp = null, // ikke relevant i anonym kontekst
            serviceberegnetAfp = null, // ditto
            privatAfpListe = dto.afpPrivatPerioder.map(::privatAfp),
            vilkaarsproeving = Vilkaarsproeving(innvilget = dto.alderspensjonPerioder.isNotEmpty(), alternativ = null),
            harForLiteTrygdetid = false, //TODO
            trygdetid = 0, // ikke relevant i anonym kontekst
            opptjeningListe = emptyList(), // ditto
            alderAar = null, // ditto
            problem = null // ditto
        )

    private fun alderspensjon(dto: SimulatorAnonymPensjonsperiode) =
        SimulertAlderspensjon(
            alder = dto.alder ?: 0,
            beloep = dto.belop ?: 0,
            inntektspensjonBeloep = 0, // ikke relevant i anonym kontekst
            delingstall = 0.0, // ditto
            pensjonBeholdningFoerUttak = 0, // ditto
            sluttpoengtall = 0.0, // ditto
            poengaarFoer92 = 0, // ditto
            poengaarEtter91 = 0, // ditto
            forholdstall = 0.0, // ditto
            grunnpensjon = 0, // ditto
            tilleggspensjon = 0, // ditto
            pensjonstillegg = 0, // ditto
            skjermingstillegg = 0, // ditto
            kapittel19Pensjon = null, // ditto
            kapittel20Pensjon = null // ditto
        )

    private fun privatAfp(dto: SimulatorAnonymPrivatAfpPeriode) =
        SimulertAfpPrivat(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0,
            kompensasjonstillegg = 0, // ikke relevant i anonym kontekst
            kronetillegg = 0, // ditto
            livsvarig = 0, // ditto
            maanedligBeloep = 0 // ditto
        )

    private fun livsvarigOffentligAfp(dto: SimulatorAnonymLivsvarigOffentligAfpPeriode) =
        SimulertAfpOffentlig(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0,
            maanedligBeloep = 0 // ikke relevant i anonym kontekst
        )

    private fun error(dto: SimulatorAnonymSimuleringError) =
        SimuleringError(
            status = dto.status,
            message = dto.message
        )
}