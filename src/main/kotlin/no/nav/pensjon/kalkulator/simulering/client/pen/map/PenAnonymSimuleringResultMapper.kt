package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.simulering.*
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymPensjonsperiode
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimuleringResult
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimulertAfpOffentligPeriode
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.PenAnonymSimulertAfpPrivatPeriode

object PenAnonymSimuleringResultMapper {

    fun fromDto(dto: PenAnonymSimuleringResult) =
        SimuleringResult(
            alderspensjon = dto.alderspensjonPerioder.map(::alderspensjon),
            afpPrivat = dto.afpPrivatPerioder.map(::afpPrivat),
            afpOffentlig = dto.afpOffentligPerioder.map(::afpOffentlig),
            vilkaarsproeving = Vilkaarsproeving(innvilget = dto.alderspensjonPerioder.isNotEmpty(), alternativ = null),
            harForLiteTrygdetid = false, //TODO
            trygdetid = 0, // not required in anonym context
            opptjeningGrunnlagListe = emptyList() // not required in anonym context
        )

    private fun alderspensjon(dto: PenAnonymPensjonsperiode) =
        SimulertAlderspensjon(
            alder = dto.alder ?: 0,
            beloep = dto.belop ?: 0,
            inntektspensjonBeloep = 0, // not required in anonym context
            garantipensjonBeloep = 0, // ditto
            delingstall = 0.0, // ditto
            pensjonBeholdningFoerUttak = 0 // ditto
        )

    private fun afpPrivat(dto: PenAnonymSimulertAfpPrivatPeriode) =
        SimulertAfpPrivat(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0
        )

    private fun afpOffentlig(dto: PenAnonymSimulertAfpOffentligPeriode) =
        SimulertAfpOffentlig(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0
        )
}
