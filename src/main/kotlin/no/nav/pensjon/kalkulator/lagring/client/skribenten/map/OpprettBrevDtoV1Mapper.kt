package no.nav.pensjon.kalkulator.lagring.client.skribenten.map

import no.nav.pensjon.kalkulator.lagring.LagreAarligBeloep
import no.nav.pensjon.kalkulator.lagring.LagreAfpPrivat
import no.nav.pensjon.kalkulator.lagring.LagreAlder
import no.nav.pensjon.kalkulator.lagring.LagreMaanedligAlderspensjon
import no.nav.pensjon.kalkulator.lagring.LagreMaanedligAlderspensjonForKnekkpunkter
import no.nav.pensjon.kalkulator.lagring.LagreSimulering
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringResponse
import no.nav.pensjon.kalkulator.lagring.LagreSimuleringsinformasjon
import no.nav.pensjon.kalkulator.lagring.LagreTidsbegrensetOffentligAfp
import no.nav.pensjon.kalkulator.lagring.LagreTrygdetid
import no.nav.pensjon.kalkulator.lagring.LagreUttaksparametre
import no.nav.pensjon.kalkulator.lagring.LagreVilkaarsproevingsresultat
import no.nav.pensjon.kalkulator.lagring.api.dto.BrevResponseDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.AarligBeloepBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.AfpPrivatBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.AlderspensjonBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.AlderBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.AlternativUttaksparametreBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.LivsvarigOffentligAfpBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.MaanedligAlderspensjonBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.MaanedligAlderspensjonForKnekkpunkterBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.OpprettBrevRequestDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.SimuleringBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.SimuleringsinformasjonBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.TidsbegrensetOffentligAfpBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.TrygdetidBrevDtoV1
import no.nav.pensjon.kalkulator.lagring.client.skribenten.dto.VilkaarsproevingsresultatBrevDtoV1

object OpprettBrevDtoV1Mapper {

    fun toDto(source: LagreSimulering) = OpprettBrevRequestDtoV1(
        brevkode = "PENSJONSKALKULATOR_AP_SIMULERING",
        spraak = "NB",
        avsenderEnhetsId = source.enhetsId,
        reserverForRedigering = false,
        saksbehandlerValg = SimuleringBrevDtoV1(
            alderspensjonListe = source.alderspensjonListe.map { AlderspensjonBrevDtoV1(it.alderAar, it.beloep, it.gjenlevendetillegg) },
            livsvarigOffentligAfpListe = source.livsvarigOffentligAfpListe?.map { LivsvarigOffentligAfpBrevDtoV1(it.alderAar, it.aarligBeloep, it.maanedligBeloep) },
            tidsbegrensetOffentligAfp = source.tidsbegrensetOffentligAfp?.let { mapToTidsbegrensetOffentligAfpDto(it)},
            privatAfpListe = source.privatAfpListe?.map { maptoPrivatAfpDto(it) },
            vilkaarsproevingsresultat = source.vilkaarsproevingsresultat?.let { mapToVilkaarsproevingsresultatDto(it) },
            trygdetid = source.trygdetid?.let{ mapToTrygdetidDto(it) },
            pensjonsgivendeInntektListe = source.pensjonsgivendeInntektListe?.map { mapToPensjonsgivendeInntektDto(it) },
            simuleringsinformasjon = source.simuleringsinformasjon?.let { mapToSimuleringsinformasjonDto(it) },
        )
    )

    fun fromDto(source: BrevResponseDtoV1) = LagreSimuleringResponse(
        brevId = source.info.id,
        sakId = source.info.saksId,
    )

    private fun mapToPensjonsgivendeInntektDto(source: LagreAarligBeloep) = AarligBeloepBrevDtoV1(
        aarstall = source.aarstall,
        beloep = source.beloep
    )

    private fun mapToTrygdetidDto(source: LagreTrygdetid) = TrygdetidBrevDtoV1(
        antallAar = source.antallAar,
        erUtilstrekkelig = source.erUtilstrekkelig
    )

    private fun mapToTidsbegrensetOffentligAfpDto(source: LagreTidsbegrensetOffentligAfp
    ): TidsbegrensetOffentligAfpBrevDtoV1 {
        TODO("Not yet implemented")
    }

    private fun maptoPrivatAfpDto(source: LagreAfpPrivat) = AfpPrivatBrevDtoV1(
        alderAar = source.alderAar,
        aarligBeloep = source.aarligBeloep,
        kompensasjonstillegg = source.kompensasjonstillegg,
        kronetillegg = source.kronetillegg,
        livsvarig = source.livsvarig,
        maanedligBeloep = source.maanedligBeloep
    )

    private fun mapToVilkaarsproevingsresultatDto(source: LagreVilkaarsproevingsresultat) =
        VilkaarsproevingsresultatBrevDtoV1(
            erInnvilget = source.erInnvilget,
            alternativ = source.alternativ?.let { mapToAlternativDto(it) }
        )

    private fun mapToAlternativDto(source: LagreUttaksparametre) = AlternativUttaksparametreBrevDtoV1(
        gradertUttakAlder = source.gradertUttakAlder?.let { mapToAlderDto(it) },
        uttaksgrad = source.uttaksgrad,
        heltUttakAlder = mapToAlderDto(source.heltUttakAlder)
    )

    private fun mapToAlderDto(source: LagreAlder) = AlderBrevDtoV1(
        aar = source.aar,
        maaneder = source.maaneder
    )

    private fun mapToSimuleringsinformasjonDto(source: LagreSimuleringsinformasjon) =
        SimuleringsinformasjonBrevDtoV1(
            gradertUttaksalder = source.gradertUttaksalder?.let { mapToAlderDto(it) },
            heltUttaksalder = source.heltUttaksalder?.let { mapToAlderDto(it) },
            maanedligAlderspensjonForKnekkpunkter = source.maanedligAlderspensjonForKnekkpunkter?.let { mapToKnekkpunkterDto(it) }
        )

    private fun mapToKnekkpunkterDto(source: LagreMaanedligAlderspensjonForKnekkpunkter) =
        MaanedligAlderspensjonForKnekkpunkterBrevDtoV1(
            vedGradertUttak = source.vedGradertUttak?.let { mapToMaanedligAlderspensjonDto(it) },
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
            kapittel19AndelTeller = source.kapittel19Andel?.let { (it * 10).toInt() },
            kapittel19Trygdetid = source.kapittel19Trygdetid,
            basispensjonBeloep = source.basispensjonBeloep,
            restpensjonBeloep = source.restpensjonBeloep,
            gjenlevendetillegg = source.gjenlevendetillegg,
            minstePensjonsnivaaSats = source.minstePensjonsnivaaSats,
            kapittel20AndelTeller = source.kapittel20Andel?.let { (it * 10).toInt() },
            kapittel20Trygdetid = source.kapittel20Trygdetid,
            garantipensjonBeloep = source.garantipensjonBeloep,
            garantipensjonSats = source.garantipensjonSats,
            garantitilleggBeloep = source.garantitilleggBeloep
        )
}