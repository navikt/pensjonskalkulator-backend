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
            pensjonBeholdningFoerUttak = 0, // ditto
            andelsbroekKap19 = 0.0, // ditto
            andelsbroekKap20 = 0.0, //
            sluttpoengtall = 0.0, // ditto
            trygdetidKap19 = 0, // ditto
            trygdetidKap20 = 0, // ditto
            poengaarFoer92 = 0, // ditto
            poengaarEtter91 = 0, // ditto
            forholdstall = 0.0, // ditto
            grunnpensjon = 0, // ditto
            tilleggspensjon = 0, // ditto
            pensjonstillegg = 0, // ditto
            skjermingstillegg = 0, // ditto
            kapittel19Gjenlevendetillegg = 0 // ditto
        )

    private fun privatAfp(dto: SimulatorAnonymPrivatAfpPeriode) =
        SimulertAfpPrivat(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0,
            kompensasjonstillegg = 0, // Ikke relevant i anonym kontekst enda
            kronetillegg = 0, // Ikke relevant i anonym kontekst enda
            livsvarig = 0, // Ikke relevant i anonym kontekst enda
            maanedligBeloep = 0 // Ikke relevant i anonym kontekst enda
        )

    private fun livsvarigOffentligAfp(dto: SimulatorAnonymLivsvarigOffentligAfpPeriode) =
        SimulertAfpOffentlig(
            alder = dto.alder ?: 0,
            beloep = dto.belopArlig ?: 0,
            maanedligBeloep = 0 // Ikke relevant i anonym kontekst enda
        )

    private fun error(dto: SimulatorAnonymSimuleringError) =
        SimuleringError(
            status = dto.status,
            message = dto.message
        )
}
