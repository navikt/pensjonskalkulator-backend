package no.nav.pensjon.kalkulator.simulering.api.v1.acl.spec

import no.nav.pensjon.kalkulator.general.*
import no.nav.pensjon.kalkulator.land.Land
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.*

/**
 * Anti-corruption.
 * Maps between data transfer objects (DTOs) and domain objects related to simulering.
 */
object SimuleringSpecMapper {

    private val defaultEpsSpec =
        EpsSpec(
            levende = LevendeEps(harInntektOver2G = false, harPensjon = false),
            avdoed = null
        )

    fun fromDto(source: SimuleringV1Spec) =
        ImpersonalSimuleringSpec(
            simuleringType = source.simuleringstype.internalValue,
            eps = source.eps?.let(::epsSpec) ?: defaultEpsSpec,
            forventetAarligInntektFoerUttak = source.aarligInntektFoerUttakBeloep,
            sivilstand = source.sivilstatus?.internalValue,
            gradertUttak = source.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(source.heltUttak),
            utenlandsopphold = utenlandsopphold(source),
            afpInntektMaanedFoerUttak = source.offentligAfp?.harInntektMaanedenFoerUttak,
            afpOrdning = source.offentligAfp?.afpOrdning?.internalValue,
            innvilgetLivsvarigOffentligAfp = source.offentligAfp?.innvilgetLivsvarigAfpListe?.firstOrNull()?.let(::afp)
        )

    private fun epsSpec(source: SimuleringV1EpsSpec) =
        EpsSpec(
            levende = source.levende?.let(::levendeEps),
            avdoed = source.avdoed?.let(::avdoedEps)
        )

    private fun gradertUttak(source: SimuleringV1GradertUttakSpec) =
        GradertUttak(
            grad = Uttaksgrad.from(source.grad),
            uttakFomAlder = alder(source.uttaksalder),
            aarligInntekt = source.aarligInntektVsaPensjonBeloep ?: 0
        )

    private fun heltUttak(source: SimuleringV1HeltUttakSpec) =
        HeltUttak(
            uttakFomAlder = alder(source.uttaksalder),
            inntekt = source.aarligInntektVsaPensjon?.let(::inntekt)
        )

    private fun utenlandsopphold(source: SimuleringV1Spec) =
        Utenlandsopphold(
            periodeListe = source.utenlandsperiodeListe.orEmpty().map(::opphold),
            antallAar = 0 // not relevant when periodeListe used
        )

    private fun opphold(source: SimuleringV1UtenlandsperiodeSpec) =
        Opphold(
            fom = source.fom,
            tom = source.tom,
            land = Land.valueOf(source.landkode), // NB: Must be corresponding 1-to-1
            arbeidet = source.arbeidetUtenlands
        )

    private fun inntekt(source: SimuleringV1InntektSpec) =
        Inntekt(
            aarligBeloep = source.beloep,
            tomAlder = source.sluttAlder.let(::alder)
        )

    private fun afp(source: SimuleringV1InnvilgetLivsvarigOffentligAfpSpec) =
        InnvilgetLivsvarigOffentligAfpSpec(
            aarligBruttoBeloep = source.aarligBruttoBeloep,
            uttakFom = source.uttakFom,
            sistRegulertGrunnbeloep = source.sistRegulertGrunnbeloep
        )

    private fun alder(source: SimuleringV1AlderSpec) =
        Alder(
            aar = source.aar,
            maaneder = source.maaneder
        )

    private fun levendeEps(source: SimuleringV1LevendeEps) =
        LevendeEps(
            harInntektOver2G = source.harInntektOver2G,
            harPensjon = source.harPensjon
        )

    private fun avdoedEps(source: SimuleringV1AvdoedEps) =
        AvdoedEps(
            pid = Pid(source.pid),
            doedsdato = source.doedsdato,
            medlemAvFolketrygden = source.medlemAvFolketrygden == true,
            inntektFoerDoedBeloep = source.inntektFoerDoedBeloep ?: 0,
            inntektErOverGrunnbeloepet = source.inntektErOverGrunnbeloepet == true,
            antallAarUtenlands = source.antallAarUtenlands ?: 0
        )
}
