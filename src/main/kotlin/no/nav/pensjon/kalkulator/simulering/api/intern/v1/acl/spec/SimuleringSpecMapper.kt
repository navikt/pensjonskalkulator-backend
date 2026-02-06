package no.nav.pensjon.kalkulator.simulering.api.intern.v1.acl.spec

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.simulering.*

/**
 * Anti-corruption.
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 */
object SimuleringSpecMapper {

    fun fromDto(source: SimuleringSpecDto) =
        ImpersonalSimuleringSpec(
            simuleringType = source.simuleringstype.internalValue,
            eps = source.eps?.let(::eps) ?: defaultEps(),
            forventetAarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            sivilstand = source.sivilstatus?.internalValue,
            gradertUttak = source.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(source.heltUttak),
            utenlandsopphold = utenlandsopphold(source),
            afpInntektMaanedFoerUttak = source.offentligAfp?.harInntektMaanedenFoerUttak,
            afpOrdning = source.offentligAfp?.afpOrdning?.internalValue,
            innvilgetLivsvarigOffentligAfp = source.offentligAfp?.innvilgetLivsvarigAfpListe?.firstOrNull()?.let(::afp)
        )

    private fun eps(source: EpsSpecDto) =
        Eps(
            harInntektOver2G = source.harInntektOver2G,
            harPensjon = source.harPensjon
        )

    private fun gradertUttak(source: GradertUttakSpecDto) =
        GradertUttak(
            grad = Uttaksgrad.from(source.grad),
            uttakFomAlder = alder(source.uttaksalder),
            aarligInntekt = source.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(source: HeltUttakSpecDto) =
        HeltUttak(
            uttakFomAlder = alder(source.uttaksalder),
            inntekt = source.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun utenlandsopphold(source: SimuleringSpecDto) =
        Utenlandsopphold(
            periodeListe = source.utenlandsperiodeListe.orEmpty().map(::opphold),
            antallAar = 0 // not relevant when periodeListe used
        )

    private fun opphold(source: UtenlandsperiodeSpecDto) =
        Opphold(
            fom = source.fom,
            tom = source.tom,
            land = Land.valueOf(source.landkode), // NB: Must be corresponding 1-to-1
            arbeidet = source.arbeidetUtenlands
        )

    private fun inntekt(source: InntektSpecDto) =
        Inntekt(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder.let(::alder)
        )

    private fun afp(source: InnvilgetLivsvarigOffentligAfpSpecDto) =
        InnvilgetLivsvarigOffentligAfpSpec(
            aarligBruttoBeloep = source.aarligBruttoBeloep,
            uttakFom = source.uttakFom,
            sistRegulertGrunnbeloep = source.sistRegulertGrunnbeloep
        )

    private fun alder(source: AlderSpecDto) =
        Alder(
            aar = source.aar,
            maaneder = source.maaneder
        )

    private fun defaultEps() =
        Eps(
            harInntektOver2G = false,
            harPensjon = false
        )
}
