package no.nav.pensjon.kalkulator.lagring.api.map

import no.nav.pensjon.kalkulator.lagring.*
import no.nav.pensjon.kalkulator.lagring.api.dto.*

object LagreSimuleringMapperV1 {

    fun toDto(source: LagreSimuleringResponse) =
        LagreSimuleringResponseDtoV1(
            brevId = source.brevId,
            sakId = source.sakId,
            brevDevQ2Url = "https://pensjon-skribenten-web-q2.intern.dev.nav.no/saksnummer/${source.sakId}/brev/${source.brevId}",
        )

    fun fromDto(source: LagreSimuleringSpecDtoV1) =
        LagreSimulering(
            alderspensjonListe = source.alderspensjonListe.map(::alderspensjon),
            afpPrivat = source.afpPrivat?.let(::afpPrivatSimulering),
            afpOffentligLivsvarig = source.afpOffentligLivsvarig?.let(::afpOffentligLivsvarigSimulering),
            afpOffentligTidsbegrenset = source.afpOffentligTidsbegrenset?.let(::afpOffentligTidsbegrensetSimulering),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproevingsresultat),
            trygdetid = source.trygdetid?.let(::trygdetid),
            pensjonsgivendeInntektListe = source.pensjonsgivendeInntektListe?.map(::aarligBeloep),
            simuleringsinformasjon = source.simuleringsinformasjon?.let(::simuleringsinformasjon),
            maanedligAlderspensjonForKnekkpunkter = source.maanedligAlderspensjonForKnekkpunkter?.let(::maanedligAlderspensjonForKnekkpunkter),
            enhetsId = source.navEnhetId ?: "4817",
        )

    private fun alderspensjon(source: LagreAlderspensjonDto) =
        LagreAlderspensjon(
            alderAar = source.alderAar,
            beloep = source.beloep,
            gjenlevendetillegg = source.gjenlevendetillegg
        )

    private fun afpPrivatSimulering(source: LagreAfpPrivatSimuleringDto) =
        LagreAfpPrivatSimulering(
            vedGradertUttak = source.vedGradertUttak?.let(::afpPrivat),
            vedHeltUttak = afpPrivat(source.vedHeltUttak),
        )

    private fun afpOffentligLivsvarigSimulering(source: LagreAfpOffentligLivsvarigSimuleringDto) =
        LagreAfpOffentligLivsvarigSimulering(
            vedGradertUttak = source.vedGradertUttak?.let(::livsvarigOffentligAfp),
            vedHeltUttak = livsvarigOffentligAfp(source.vedHeltUttak),
        )

    private fun afpOffentligTidsbegrensetSimulering(source: LagreAfpOffentligTidsbegrensetSimuleringDto) =
        LagreAfpOffentligTidsbegrensetSimulering(
            vedGradertUttak = source.vedGradertUttak?.let(::tidsbegrensetOffentligAfp),
            vedHeltUttak = tidsbegrensetOffentligAfp(source.vedHeltUttak),
        )

    private fun livsvarigOffentligAfp(source: LagreLivsvarigOffentligAfpDto) =
        LagreLivsvarigOffentligAfp(
            alderAar = source.alderAar,
            aarligBeloep = source.aarligBeloep,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun tidsbegrensetOffentligAfp(source: LagreTidsbegrensetOffentligAfpDto) =
        LagreTidsbegrensetOffentligAfp(
            alderAar = source.alderAar,
            totaltAfpBeloep = source.totaltAfpBeloep,
            tidligereArbeidsinntekt = source.tidligereArbeidsinntekt,
            grunnbeloep = source.grunnbeloep,
            sluttpoengtall = source.sluttpoengtall,
            trygdetid = source.trygdetid,
            poengaarTom1991 = source.poengaarTom1991,
            poengaarFom1992 = source.poengaarFom1992,
            grunnpensjon = source.grunnpensjon,
            tilleggspensjon = source.tilleggspensjon,
            afpTillegg = source.afpTillegg,
            saertillegg = source.saertillegg,
            afpGrad = source.afpGrad,
            erAvkortet = source.erAvkortet
        )

    private fun afpPrivat(source: LagrePrivatAfpDto) =
        LagreAfpPrivat(
            alderAar = source.alderAar,
            aarligBeloep = source.aarligBeloep,
            kompensasjonstillegg = source.kompensasjonstillegg,
            kronetillegg = source.kronetillegg,
            livsvarig = source.livsvarig,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun vilkaarsproevingsresultat(source: LagreVilkaarsproevingsresultatDto) =
        LagreVilkaarsproevingsresultat(
            erInnvilget = source.erInnvilget,
            alternativ = source.alternativ?.let(::uttaksparametre)
        )

    private fun uttaksparametre(source: LagreUttaksparametreDto) =
        LagreUttaksparametre(
            gradertUttakAlder = source.gradertUttakAlder?.let(::alder),
            uttaksgrad = source.uttaksgrad,
            heltUttakAlder = alder(source.heltUttakAlder)
        )

    private fun trygdetid(source: LagreTrygdetidDto) =
        LagreTrygdetid(
            antallAar = source.antallAar,
            erUtilstrekkelig = source.erUtilstrekkelig
        )

    private fun aarligBeloep(source: LagreAarligBeloepDto) =
        LagreAarligBeloep(
            aarstall = source.aarstall,
            beloep = source.beloep
        )

    private fun alder(source: LagreAlderDto) =
        LagreAlder(
            aar = source.aar,
            maaneder = source.maaneder
        )

    private fun simuleringsinformasjon(source: LagreSimuleringsinformasjonDto) =
        LagreSimuleringsinformasjon(
            gradertUttaksalder = source.gradertUttaksalder?.let(::alder),
            heltUttaksalder = alder(source.heltUttaksalder),
            sivilstatus = source.sivilstatus,
            utenlandsperioder = source.utenlandsperioder?.map(::utenlandsperiode),
            kull = source.kull,
            normertPensjonsalderPlassering = source.normertPensjonsalderPlassering
        )

    private fun utenlandsperiode(source: LagreUtenlandsperiodeDto) =
        LagreUtenlandsperiode(
            fom = source.fom,
            tom = source.tom,
            landkode = source.landkode,
            arbeidetUtenlands = source.arbeidetUtenlands
        )

    private fun maanedligAlderspensjonForKnekkpunkter(source: LagreMaanedligAlderspensjonForKnekkpunkterDto) =
        LagreMaanedligAlderspensjonForKnekkpunkter(
            vedGradertUttak = source.vedGradertUttak?.let(::maanedligAlderspensjon),
            vedHeltUttak = maanedligAlderspensjon(source.vedHeltUttak),
            vedNormertPensjonsalder = maanedligAlderspensjon(source.vedNormertPensjonsalder)
        )

    private fun maanedligAlderspensjon(source: LagreMaanedligAlderspensjonDto) =
        LagreMaanedligAlderspensjon(
            beloep = source.beloep,
            inntektspensjonBeloep = source.inntektspensjonBeloep,
            delingstall = source.delingstall,
            pensjonsbeholdningFoerUttakBeloep = source.pensjonsbeholdningFoerUttakBeloep,
            pensjonsbeholdningEtterUttakBeloep = source.pensjonsbeholdningEtterUttakBeloep,
            sluttpoengtall = source.sluttpoengtall,
            poengaarTom1991 = source.poengaarTom1991,
            poengaarFom1992 = source.poengaarFom1992,
            forholdstall = source.forholdstall,
            grunnpensjonBeloep = source.grunnpensjonBeloep,
            tilleggspensjonBeloep = source.tilleggspensjonBeloep,
            pensjonstillegg = source.pensjonstillegg,
            skjermingstillegg = source.skjermingstillegg,
            kapittel19AndelTeller = source.kapittel19AndelTeller,
            kapittel19Trygdetid = source.kapittel19Trygdetid,
            basispensjonBeloep = source.basispensjonBeloep,
            restpensjonBeloep = source.restpensjonBeloep,
            gjenlevendetillegg = source.gjenlevendetillegg,
            minstePensjonsnivaaSats = source.minstePensjonsnivaaSats,
            minstePensjonsnivaaBeloep = source.minstePensjonsnivaaBeloep,
            kapittel20AndelTeller = source.kapittel20AndelTeller,
            kapittel20Trygdetid = source.kapittel20Trygdetid,
            garantipensjonBeloep = source.garantipensjonBeloep,
            garantipensjonsnivaaBeloep = source.garantipensjonsnivaaBeloep,
            garantipensjonSats = source.garantipensjonSats,
            garantitilleggBeloep = source.garantitilleggBeloep,
            grunnbeloep = source.grunnbeloep
        )
}
