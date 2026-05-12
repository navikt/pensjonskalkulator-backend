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
            livsvarigOffentligAfpListe = source.livsvarigOffentligAfpListe.orEmpty().map(::afpOffentlig),
            tidsbegrensetOffentligAfp = source.tidsbegrensetOffentligAfp?.let(::tidsbegrensetOffentligAfp),
            privatAfpListe = source.privatAfpListe.orEmpty().map(::afpPrivat),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproevingsresultat),
            trygdetid = source.trygdetid?.let(::trygdetid),
            pensjonsgivendeInntektListe = source.pensjonsgivendeInntektListe.orEmpty().map(::aarligBeloep),
            simuleringsinformasjon = source.simuleringsinformasjon?.let(::simuleringsinformasjon),
            enhetsId = source.navEnhetId ?: "4817", //pensjon-pen: SendStandardBrevServiceImpl.NAV_PENSJON_TKNR = arrayOf("4803", "4808", "4815", "4817")
        )

    private fun alderspensjon(source: LagreAlderspensjonDto) =
        LagreAlderspensjon(
            alderAar = source.alderAar,
            beloep = source.beloep,
            gjenlevendetillegg = source.gjenlevendetillegg
        )

    private fun afpOffentlig(source: LagreAldersbestemtUtbetalingDto) =
        LagreAfpOffentlig(
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
            heltUttaksalder = source.heltUttaksalder?.let(::alder),
            maanedligAlderspensjonForKnekkpunkter = source.maanedligAlderspensjonForKnekkpunkter?.let(::maanedligAlderspensjonForKnekkpunkter)
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
            kapittel19Andel = source.kapittel19Andel,
            kapittel19Trygdetid = source.kapittel19Trygdetid,
            basispensjonBeloep = source.basispensjonBeloep,
            restpensjonBeloep = source.restpensjonBeloep,
            gjenlevendetillegg = source.gjenlevendetillegg,
            minstePensjonsnivaaSats = source.minstePensjonsnivaaSats,
            kapittel20Andel = source.kapittel20Andel,
            kapittel20Trygdetid = source.kapittel20Trygdetid,
            garantipensjonBeloep = source.garantipensjonBeloep,
            garantipensjonSats = source.garantipensjonSats,
            garantitilleggBeloep = source.garantitilleggBeloep
        )
}
