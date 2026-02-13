package no.nav.pensjon.kalkulator.simulering.client.simulator.acl.spec

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.simulering.AvdoedEps
import no.nav.pensjon.kalkulator.simulering.EpsSpec
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.InnvilgetLivsvarigOffentligAfpSpec
import no.nav.pensjon.kalkulator.simulering.LevendeEps
import no.nav.pensjon.kalkulator.simulering.Opphold
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.client.simulator.acl.UttaksgradDto

object PersonligSimuleringSpecMapper {

    fun toDto(
        impersonalSpec: ImpersonalSimuleringSpec,
        personalSpec: PersonalSimuleringSpec
    ) =
        PersonligSimuleringSpecDto(
            pid = personalSpec.pid.value,
            sivilstatus = SivilstatusSpecDto.fromInternalValue(personalSpec.sivilstand),
            simuleringstype = SimuleringstypeSpecDto.fromInternalValue(impersonalSpec.simuleringType),
            sisteInntekt = personalSpec.aarligInntektFoerUttak,
            gradertUttak = impersonalSpec.gradertUttak?.let(::gradertUttak),
            heltUttak = heltUttak(impersonalSpec.heltUttak),
            aarUtenlandsEtter16Aar = null,
            fremtidigInntektListe = null,
            utenlandsperiodeListe = impersonalSpec.utenlandsopphold.periodeListe.map(::utlandPeriode),
            eps = eps(impersonalSpec.eps),
            offentligAfp = offentligAfp(impersonalSpec)
        )

    private fun gradertUttak(source: GradertUttak) =
        GradertUttakSpecDto(
            grad = UttaksgradDto.fromInternalValue(source.grad),
            uttakFomAlder = alder(source.uttakFomAlder),
            aarligInntekt = source.aarligInntekt
        )

    private fun heltUttak(source: HeltUttak) =
        HeltUttakSpecDto(
            uttakFomAlder = alder(source.uttakFomAlder!!), // mandatory in the context of simulering
            aarligInntekt = source.inntekt?.aarligBeloep ?: 0,
            inntektTomAlder = source.inntekt?.let { alder(it.tomAlder) } ?: alder(source.uttakFomAlder)
        )

    private fun utlandPeriode(source: Opphold) =
        UtlandSpecDto(
            fom = source.fom,
            tom = source.tom,
            land = source.land.name,
            arbeidetUtenlands = source.arbeidet
        )

    private fun offentligAfp(source: ImpersonalSimuleringSpec) =
        OffentligAfpSpecDto(
            harInntektMaanedenFoerUttak = source.afpInntektMaanedFoerUttak,
            afpOrdning = source.afpOrdning?.let(AfpOrdningTypeSpecDto::fromInternalValue),
            innvilgetLivsvarigAfp = source.innvilgetLivsvarigOffentligAfp?.let(::livsvarigOffentligAfp)
        )

    private fun livsvarigOffentligAfp(source: InnvilgetLivsvarigOffentligAfpSpec) =
        InnvilgetLivsvarigOffentligAfpSpecDto(
            aarligBruttoBeloep = source.aarligBruttoBeloep,
            uttakFom = source.uttakFom,
            sistRegulertGrunnbeloep = source.sistRegulertGrunnbeloep
        )

    private fun eps(source: EpsSpec) =
        EpsSpecDto(
            levende = source.levende?.let(::levendeEps),
            avdoed = source.avdoed?.let(::avdoedEps)
        )

    private fun levendeEps(source: LevendeEps) =
        LevendeEpsSpecDto(
            harInntektOver2G = source.harInntektOver2G,
            harPensjon = source.harPensjon
        )

    private fun avdoedEps(source: AvdoedEps) =
        AvdoedEpsSpecDto(
            pid = source.pid.value,
            doedsdato = source.doedsdato,
            medlemAvFolketrygden = source.medlemAvFolketrygden,
            inntektFoerDoedBeloep = source.inntektFoerDoedBeloep,
            inntektErOverGrunnbeloepet = source.inntektErOverGrunnbeloepet,
            antallAarUtenlands = source.antallAarUtenlands
        )

    private fun alder(source: Alder) =
        AlderSpecDto(aar = source.aar, maaneder = source.maaneder)
}
