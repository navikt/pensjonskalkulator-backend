package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.Sivilstatus
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.*
import no.nav.pensjon.kalkulator.general.Alder

object PensjonsavtaleMapper {

    private const val DEFAULT_VALUE = "ukjent"
    private val DEFAULT_UTTAKSGRAD = Uttaksgrad.HUNDRE_PROSENT
    private val DEFAULT_HAR_EPS_PENSJON = true // Norsk Pensjon default
    private val DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G = true // Norsk Pensjon default

    fun toDto(spec: PensjonsavtaleSpec) =
        NorskPensjonPensjonsavtaleSpecDto(
            pid = spec.pid,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttak,
            uttaksperioder = spec.uttaksperioder.map(::toUttaksperiodeEgressSpecDto),
            antallInntektsaarEtterUttak = spec.antallInntektsaarEtterUttak,
            harAfp = spec.harAfp,
            harEpsPensjon = spec.harEpsPensjon ?: DEFAULT_HAR_EPS_PENSJON,
            harEpsPensjonsgivendeInntektOver2G = spec.harEpsPensjonsgivendeInntektOver2G
                ?: DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G,
            antallAarIUtlandetEtter16 = spec.antallAarIUtlandetEtter16,
            sivilstatus = Sivilstatus.fromInternalValue(spec.sivilstatus),
            oenskesSimuleringAvFolketrygd = spec.oenskesSimuleringAvFolketrygd
        )

    // Norsk Pensjon bruker månedsverdier 1..12 (dermed '+ 1')
    private fun toUttaksperiodeEgressSpecDto(spec: UttaksperiodeSpec) =
        NorskPensjonUttaksperiodeSpecDto(
            start = NorskPensjonAlderDto(spec.start.aar, spec.start.maaneder + 1),
            grad = spec.grad,
            aarligInntekt = spec.aarligInntekt
        )

    fun fromDto(dto: EnvelopeDto) =
        Pensjonsavtaler(
            pensjonsavtaler(dto) ?: emptyOrFault(dto),
            utilgjengeligeSelskap(dto) ?: emptyList()
        )

    fun faultToString(fault: FaultDto) =
        fault.let {
            "Code: ${it.faultcode} | String: ${it.faultstring} | Actor: ${it.faultactor}" +
                    " | Detail: { ${nullableDetailToString(it.detail)} }"
        }

    private fun pensjonsavtaler(dto: EnvelopeDto) =
        dto.body?.pensjonsrettigheter?.pensjonsRettigheter?.map {
            Pensjonsavtale(
                it.avtalenummer ?: "",
                it.arbeidsgiver ?: DEFAULT_VALUE,
                it.selskapsnavn ?: DEFAULT_VALUE,
                it.produktbetegnelse ?: DEFAULT_VALUE,
                Kategori.fromExternalValue(it.kategori).internalValue,
                Underkategori.fromExternalValue(it.underkategori).internalValue,
                it.innskuddssaldo ?: 0,
                it.naavaerendeAvtaltAarligInnskudd ?: 0,
                it.pensjonsbeholdningForventet ?: 0,
                it.pensjonsbeholdningNedreGrense ?: 0,
                it.pensjonsbeholdningOvreGrense ?: 0,
                it.avkastningsgaranti ?: false,
                Beregningsmodell.fromExternalValue(it.beregningsmodell).internalValue,
                it.startAlder ?: 0,
                it.sluttAlder,
                it.opplysningsdato ?: DEFAULT_VALUE,
                it.aarsakManglendeGradering?.internalValue ?: ManglendeEksternGraderingAarsak.NONE,
                it.aarsakIkkeBeregnet?.internalValue ?: ManglendeEksternBeregningAarsak.NONE,
                it.utbetalingsperioder?.map(::utbetalingsperiode) ?: emptyList()
            )
        }

    private fun utilgjengeligeSelskap(dto: EnvelopeDto) =
        dto.body?.pensjonsrettigheter?.utilgjengeligeInnretninger?.map {
            Selskap(
                it.selskapsnavn ?: DEFAULT_VALUE,
                it.heltUtilgjengelig ?: false,
                it.antallManglendeRettigheter ?: 0,
                Kategori.fromExternalValue(it.kategori).internalValue,
                it.feilkode ?: ""
            )
        }

    private fun utbetalingsperiode(source: UtbetalingsperiodeDto) =
        Utbetalingsperiode(
            Alder(source.startAlder!!, source.startMaaned!! - 1),
            source.sluttAlder?.let { Alder(it, source.sluttMaaned!! - 1) },
            source.aarligUtbetalingForventet ?: 0,
            source.aarligUtbetalingNedreGrense ?: 0,
            source.aarligUtbetalingOvreGrense ?: 0,
            source.grad?.let { Uttaksgrad.from(it) } ?: DEFAULT_UTTAKSGRAD
        )

    private fun emptyOrFault(dto: EnvelopeDto) =
        dto.body?.fault?.run { throw RuntimeException(faultToString(this)) }
            ?: emptyList<Pensjonsavtale>()

    private fun nullableDetailToString(detail: FaultDetailDto?) = detail?.let(::detailToString) ?: "no detail"

    private fun detailToString(detail: FaultDetailDto) =
        detail.let {
            "Transaction: ${it.transactionId} | Global transaction: ${it.globalTransactionId}"
        }
}
