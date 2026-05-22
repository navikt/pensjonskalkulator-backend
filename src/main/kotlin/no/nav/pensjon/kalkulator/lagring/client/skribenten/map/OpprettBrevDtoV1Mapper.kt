package no.nav.pensjon.kalkulator.lagring.client.skribenten.map

import no.nav.pensjon.kalkulator.lagring.*
import no.nav.pensjon.kalkulator.lagring.api.dto.BrevResponseDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.*

object OpprettBrevDtoV1Mapper {

    fun toDto(source: LagreSimulering, forbehold: ForbeholdInnhold?) = OpprettBrevRequestDtoV1(
        brevkode = "PENSJONSKALKULATOR_AP_SIMULERING",
        spraak = "NB",
        avsenderEnhetsId = source.enhetsId,
        reserverForRedigering = false,
        saksbehandlerValg = SimuleringBrevDtoV1(
            simulering = SimuleringBrevV1(
                alderspensjonListe = source.alderspensjonListe.map { AlderspensjonBrevDtoV1(it.alderAar, it.beloep, it.gjenlevendetillegg) },
                maanedligAlderspensjonForKnekkpunkter = source.maanedligAlderspensjonForKnekkpunkter?.let(::mapToKnekkpunkterDto),
                afpPrivat = source.afpPrivat?.let(::mapToAfpPrivatSimuleringDto),
                afpOffentligLivsvarig = source.afpOffentligLivsvarig?.let(::mapToAfpOffentligLivsvarigSimuleringDto),
                afpOffentligTidsbegrenset = source.afpOffentligTidsbegrenset?.let(::mapToAfpOffentligTidsbegrensetSimuleringDto),
            ),
            simuleringsinformasjon = source.simuleringsinformasjon?.let(::mapToSimuleringsinformasjonDto),
            vilkaarsproevingsresultat = source.vilkaarsproevingsresultat?.let(::mapToVilkaarsproevingsresultatDto),
            trygdetid = source.trygdetid?.let(::mapToTrygdetidDto),
            pensjonsgivendeInntektListe = source.pensjonsgivendeInntektListe?.map(::mapToPensjonsgivendeInntektDto),
            forbehold = forbehold?.let(::mapToForbeholdDto),
        )
    )

    fun fromDto(source: BrevResponseDtoV1) = LagreSimuleringResponse(
        brevId = source.info.id,
        sakId = source.info.saksId,
    )

    private fun mapToAfpPrivatSimuleringDto(source: LagreAfpPrivatSimulering) =
        AfpPrivatSimuleringBrevDtoV1(
            vedGradertUttak = source.vedGradertUttak?.let(::mapToPrivatAfpDto),
            vedHeltUttak = mapToPrivatAfpDto(source.vedHeltUttak),
        )

    private fun mapToAfpOffentligLivsvarigSimuleringDto(source: LagreAfpOffentligLivsvarigSimulering) =
        AfpOffentligLivsvarigSimuleringBrevDtoV1(
            vedGradertUttak = source.vedGradertUttak?.let(::mapToLivsvarigOffentligAfpDto),
            vedHeltUttak = mapToLivsvarigOffentligAfpDto(source.vedHeltUttak),
        )

    private fun mapToAfpOffentligTidsbegrensetSimuleringDto(source: LagreAfpOffentligTidsbegrensetSimulering) =
        AfpOffentligTidsbegrensetSimuleringBrevDtoV1(
            vedGradertUttak = source.vedGradertUttak?.let(::mapToTidsbegrensetOffentligAfpDto),
            vedHeltUttak = mapToTidsbegrensetOffentligAfpDto(source.vedHeltUttak),
        )

    private fun mapToLivsvarigOffentligAfpDto(source: LagreLivsvarigOffentligAfp) =
        LivsvarigOffentligAfpBrevDtoV1(
            alderAar = source.alderAar,
            aarligBeloep = source.aarligBeloep,
            maanedligBeloep = source.maanedligBeloep
        )

    private fun mapToTidsbegrensetOffentligAfpDto(source: LagreTidsbegrensetOffentligAfp) =
        TidsbegrensetOffentligAfpBrevDtoV1(
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

    private fun mapToPrivatAfpDto(source: LagreAfpPrivat) = AfpPrivatBrevDtoV1(
        alderAar = source.alderAar,
        aarligBeloep = source.aarligBeloep,
        kompensasjonstillegg = source.kompensasjonstillegg,
        kronetillegg = source.kronetillegg,
        livsvarig = source.livsvarig,
        maanedligBeloep = source.maanedligBeloep
    )

    private fun mapToPensjonsgivendeInntektDto(source: LagreAarligBeloep) = AarligBeloepBrevDtoV1(
        aarstall = source.aarstall,
        beloep = source.beloep
    )

    private fun mapToTrygdetidDto(source: LagreTrygdetid) = TrygdetidBrevDtoV1(
        antallAar = source.antallAar,
        erUtilstrekkelig = source.erUtilstrekkelig
    )

    private fun mapToVilkaarsproevingsresultatDto(source: LagreVilkaarsproevingsresultat) =
        VilkaarsproevingsresultatBrevDtoV1(
            erInnvilget = source.erInnvilget,
            alternativ = source.alternativ?.let(::mapToAlternativDto)
        )

    private fun mapToAlternativDto(source: LagreUttaksparametre) = AlternativUttaksparametreBrevDtoV1(
        gradertUttakAlder = source.gradertUttakAlder?.let(::mapToAlderDto),
        uttaksgrad = source.uttaksgrad,
        heltUttakAlder = mapToAlderDto(source.heltUttakAlder)
    )

    private fun mapToAlderDto(source: LagreAlder) = AlderBrevDtoV1(
        aar = source.aar,
        maaneder = source.maaneder
    )

    private fun mapToSimuleringsinformasjonDto(source: LagreSimuleringsinformasjon) =
        SimuleringsinformasjonBrevDtoV1(
            gradertUttaksalder = source.gradertUttaksalder?.let(::mapToAlderDto),
            heltUttaksalder = mapToAlderDto(source.heltUttaksalder),
            sivilstatus = source.sivilstatus,
            utenlandsperioder = source.utenlandsperioder?.map(::mapToUtenlandsperiodeDto),
            kull = source.kull.name,
            normertPensjonsalderPlassering = source.normertPensjonsalderPlassering?.name
        )

    private fun mapToUtenlandsperiodeDto(source: LagreUtenlandsperiode) =
        UtenlandsperiodeBrevDtoV1(
            fom = source.fom,
            tom = source.tom,
            landkode = source.landkode,
            arbeidetUtenlands = source.arbeidetUtenlands
        )

    private fun mapToKnekkpunkterDto(source: LagreMaanedligAlderspensjonForKnekkpunkter) =
        MaanedligAlderspensjonForKnekkpunkterBrevDtoV1(
            vedGradertUttak = source.vedGradertUttak?.let(::mapToMaanedligAlderspensjonDto),
            vedHeltUttak = mapToMaanedligAlderspensjonDto(source.vedHeltUttak),
            vedNormertPensjonsalder = mapToMaanedligAlderspensjonDto(source.vedNormertPensjonsalder)
        )

    private fun mapToMaanedligAlderspensjonDto(source: LagreMaanedligAlderspensjon) =
        MaanedligAlderspensjonBrevDtoV1(
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

    private fun mapToForbeholdDto(source: ForbeholdInnhold) =
        ForbeholdBrevDtoV1(
            seksjoner = source.seksjoner.map { seksjon ->
                ForbeholdSeksjonBrevDtoV1(
                    tittel = seksjon.tittel,
                    avsnitt = seksjon.avsnitt.map { avsnitt ->
                        ForbeholdAvsnittBrevDtoV1(
                            tekst = avsnitt.tekst,
                            punktliste = avsnitt.punktliste
                        )
                    }
                )
            }
        )
}
