package no.nav.pensjon.kalkulator.vedtak.api.v2.acl

import no.nav.pensjon.kalkulator.common.api.acl.CommonV1Sivilstatus
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.vedtak.*
import java.math.BigDecimal

object VedtakResultMapper {

    fun toDto(source: VedtakSamling) =
        VedtakV2Samling(
            harVedtak = source.hasContent(),
            loependeAlderspensjon = source.loependeAlderspensjon?.let(::loependeAlderspensjon),
            fremtidigAlderspensjon = source.fremtidigAlderspensjon?.let(::fremtidigAlderspensjon),
            ufoeretrygdgrad = source.ufoeretrygd?.grad,
            privatAfpFom = source.privatAfp?.fom,
            tidsbegrensetOffentligAfpFom = source.tidsbegrensetOffentligAfp?.fom,
            gjenlevenderett = source.gjenlevenderett?.let(::gjenlevenderett),
            avdoed = source.avdoed?.let(::avdoed)
        )

    private fun loependeAlderspensjon(source: LoependeAlderspensjon) =
        VedtakV2LoependeAlderspensjon(
            grad = source.grad.prosentsats,
            fom = source.fom,
            uttaksgradFom = source.uttaksgradFom ?: source.fom,
            sisteUtbetaling = source.utbetalingSisteMaaned?.let(::utbetaling),
            sivilstatus = CommonV1Sivilstatus.fromInternalValue(source.sivilstatus),
            harGjenlevenderett = source.harGjenlevenderett,
            harUtenlandsopphold = source.harUtenlandsopphold,
            livsvarigOffentligAfp = source.livsvarigOffentligAfp
        )

    private fun fremtidigAlderspensjon(source: FremtidigAlderspensjon) =
        VedtakV2Alderspensjonsuttak(
            grad = source.grad.prosentsats,
            fom = source.fom,
            livsvarigOffentligAfp = source.livsvarigOffentligAfp
        )

    private fun utbetaling(source: Utbetaling) =
        VedtakV2Utbetaling(
            beloep = source.beloep ?: BigDecimal.ZERO,
            utbetalingsdato = source.posteringsdato
        )

    private fun gjenlevenderett(source: Gjenlevenderett) =
        VedtakV2Gjenlevenderett(
            avdoedPid = source.avdoedPid.value,
            avdoedNavn = source.avdoedNavn?.let(::navn),
            doedsdato = source.doedsdato,
            foersteVirkningsdato = source.foersteVirkningsdato
        )

    private fun avdoed(source: InformasjonOmAvdoed) =
        VedtakV2InformasjonOmAvdoed(
            pid = source.pid?.value,
            doedsdato = source.doedsdato,
            foersteAlderspensjonVirkningsdato = source.foersteAlderspensjonVirkningsdato,
            aarligPensjonsgivendeInntektErMinst1G = source.aarligPensjonsgivendeInntektErMinst1G,
            harTilstrekkeligMedlemskapIFolketrygden = source.harTilstrekkeligMedlemskapIFolketrygden,
            antallAarUtenlands = source.antallAarUtenlands,
            erFlyktning = source.erFlyktning
        )

    private fun navn(source: Navn) =
        VedtakV2Navn(
            fornavn = source.fornavn,
            mellomnavn = source.mellomnavn,
            etternavn = source.etternavn
        )
}
