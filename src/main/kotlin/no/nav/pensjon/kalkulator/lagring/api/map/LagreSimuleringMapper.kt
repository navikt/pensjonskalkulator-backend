package no.nav.pensjon.kalkulator.lagring.api.map

import no.nav.pensjon.kalkulator.lagring.*
import no.nav.pensjon.kalkulator.lagring.api.dto.*

object LagreSimuleringMapper {

    fun fromDto(source: LagreSimuleringSpecDto) =
        LagreSimulering(
            alderspensjonListe = source.alderspensjonListe.map(::alderspensjon),
            livsvarigOffentligAfpListe = source.livsvarigOffentligAfpListe.orEmpty().map(::afpOffentlig),
            tidsbegrensetOffentligAfp = source.tidsbegrensetOffentligAfp?.let(::tidsbegrensetOffentligAfp),
            privatAfpListe = source.privatAfpListe.orEmpty().map(::afpPrivat),
            vilkaarsproevingsresultat = vilkaarsproevingsresultat(source.vilkaarsproevingsresultat),
            trygdetid = source.trygdetid?.let(::trygdetid),
            pensjonsgivendeInntektListe = source.pensjonsgivendeInntektListe.orEmpty().map(::aarligBeloep)
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
}
