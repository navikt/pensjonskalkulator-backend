package no.nav.pensjon.kalkulator.vedtak.client.pen.map

import no.nav.pensjon.kalkulator.common.client.pen.PenSivilstand
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.vedtak.*
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeUfoeregradDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakApDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenGjeldendeVedtakDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenInformasjonOmAvdoedDto
import no.nav.pensjon.kalkulator.vedtak.client.pen.dto.PenLoependeVedtakDto
import java.time.LocalDate

/**
 * Maps from a data transfer object (DTO) to a domain object.
 * The DTO represents 'løpende vedtak' received from PEN.
 */
object PenLoependeVedtakMapper {

    fun fromDto(source: PenLoependeVedtakDto) =
        VedtakSamling(
            loependeAlderspensjon = source.alderspensjon?.let {
                loependeAlderspensjon(source = it, uttaksgradFom = source.gjeldendeUttaksgradFom)
            },
            fremtidigAlderspensjon = fremtidigUttakgradsendring(source),
            ufoeretrygd = source.ufoeretrygd?.let(::ufoeretrygd),
            privatAfp = source.afpPrivat?.let(::vedtak),
            tidsbegrensetOffentligAfp = source.afpOffentlig?.let(::vedtak),
            avdoed = source.avdoed?.let(::avdoed)
        )

    private fun vedtak(source: PenGjeldendeVedtakDto) =
        LoependeEntitet(fom = source.fraOgMed)

    private fun loependeAlderspensjon(source: PenGjeldendeVedtakApDto, uttaksgradFom: LocalDate?) =
        LoependeAlderspensjon(
            grad = Uttaksgrad.from(prosentsats = source.grad),
            fom = source.fraOgMed,
            uttaksgradFom = uttaksgradFom ?: source.fraOgMed,
            sivilstatus = PenSivilstand.toInternalValue(source.sivilstatus),
            harGjenlevenderett = source.harGjenlevenderett,
            harUtenlandsopphold = source.harUtenlandsopphold
        )

    private fun fremtidigAlderspensjon(source: PenGjeldendeVedtakApDto) =
        FremtidigAlderspensjon(
            grad = Uttaksgrad.from(prosentsats = source.grad),
            fom = source.fraOgMed,
            sivilstatus = PenSivilstand.toInternalValue(source.sivilstatus)
        )

    private fun fremtidigUttakgradsendring(source: PenLoependeVedtakDto) =
        source.alderspensjonIFremtid
            ?.takeIf { it.grad != source.alderspensjon?.grad }
            ?.let(::fremtidigAlderspensjon)

    private fun ufoeretrygd(source: PenGjeldendeUfoeregradDto) =
        LoependeUfoeretrygd(
            grad = source.grad,
            fom = source.fraOgMed
        )

    private fun avdoed(source: PenInformasjonOmAvdoedDto) =
        InformasjonOmAvdoed(
            pid = source.pid?.let(::Pid),
            doedsdato = source.doedsdato,
            foersteAlderspensjonVirkningsdato = source.foersteVirkningsdato,
            aarligPensjonsgivendeInntektErMinst1G = source.aarligPensjonsgivendeInntektErMinst1G,
            harTilstrekkeligMedlemskapIFolketrygden = source.harTilstrekkeligMedlemskapIFolketrygden,
            antallAarUtenlands = source.antallAarUtenlands,
            erFlyktning = source.erFlyktning
        )
}