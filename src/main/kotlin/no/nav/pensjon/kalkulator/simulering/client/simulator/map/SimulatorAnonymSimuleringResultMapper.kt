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
            alderspensjon = dto.alderspensjonPerioder.map(::alderspensjon),
            afpPrivat = dto.afpPrivatPerioder.map(::privatAfp),
            afpOffentlig = dto.afpOffentligPerioder.map(::livsvarigOffentligAfp),
            vilkaarsproeving = Vilkaarsproeving(innvilget = dto.alderspensjonPerioder.isNotEmpty(), alternativ = null),
            harForLiteTrygdetid = false, //TODO
            trygdetid = 0, // not required in anonym context
            opptjeningGrunnlagListe = emptyList() // not required in anonym context
        )

    private fun alderspensjon(dto: SimulatorAnonymPensjonsperiode) =
        SimulertAlderspensjon(
            alder = dto.alder ?: 0,
            beloep = dto.belop ?: 0,
            inntektspensjonBeloep = 0, // not required in anonym context
            garantipensjonBeloep = 0, // ditto
            delingstall = 0.0, // ditto
            pensjonBeholdningFoerUttak = 0 // ditto
        )

    private fun privatAfp(dto: SimulatorAnonymPrivatAfpPeriode) =
        SimulertAfpPrivat(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0
        )

    private fun livsvarigOffentligAfp(dto: SimulatorAnonymLivsvarigOffentligAfpPeriode) =
        SimulertAfpOffentlig(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0
        )

    private fun error(dto: SimulatorAnonymSimuleringError) =
        SimuleringError(
            status = dto.status,
            message = dto.message
        )
}
