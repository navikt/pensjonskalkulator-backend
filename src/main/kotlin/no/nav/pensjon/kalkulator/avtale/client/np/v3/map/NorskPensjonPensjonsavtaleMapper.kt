package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.Sivilstatus
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.*
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid

object NorskPensjonPensjonsavtaleMapper {

    private const val DEFAULT_VALUE = "ukjent"
    private val DEFAULT_UTTAKSGRAD = Uttaksgrad.HUNDRE_PROSENT
    private val DEFAULT_HAR_EPS_PENSJON = true // Norsk Pensjon default
    private val DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G = true // Norsk Pensjon default

    fun toDto(spec: PensjonsavtaleSpec, pid: Pid) =
        NorskPensjonPensjonsavtaleSpecDto(
            pid = pid,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttak,
            uttaksperioder = spec.uttaksperioder.map(::toUttaksperiodeEgressSpecDto),
            antallInntektsaarEtterUttak = spec.antallInntektsaarEtterUttak,
            harAfp = spec.harAfp,
            harEpsPensjon = spec.harEpsPensjon ?: DEFAULT_HAR_EPS_PENSJON,
            harEpsPensjonsgivendeInntektOver2G = spec.harEpsPensjonsgivendeInntektOver2G
                ?: DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G,
            antallAarIUtlandetEtter16 = spec.antallAarIUtlandetEtter16,
            sivilstatus = Sivilstatus.fromInternalValue(spec.sivilstand),
            oenskesSimuleringAvFolketrygd = false
        )

    // Norsk Pensjon bruker månedsverdier 1..12 (dermed '+ 1')
    private fun toUttaksperiodeEgressSpecDto(spec: UttaksperiodeSpec) =
        NorskPensjonUttaksperiodeSpecDto(
            startAlder = NorskPensjonAlderDto(spec.startAlder.aar, spec.startAlder.maaneder + 1),
            grad = spec.grad,
            aarligInntekt = spec.aarligInntekt
        )

    fun fromDto(dto: EnvelopeDto) =
        Pensjonsavtaler(
            avtaler = pensjonsavtaler(dto) ?: emptyOrFault(dto),
            utilgjengeligeSelskap = utilgjengeligeSelskap(dto) ?: emptyList()
        )

    fun faultToString(fault: FaultDto) =
        fault.let {
            "Code: ${it.faultcode} | String: ${it.faultstring} | Actor: ${it.faultactor}" +
                    " | Detail: { ${nullableDetailToString(it.detail)} }"
        }

    private fun pensjonsavtaler(dto: EnvelopeDto) =
        dto.body?.pensjonsrettigheter?.pensjonsRettigheter?.map {
            Pensjonsavtale(
                avtalenummer = it.avtalenummer ?: "",
                arbeidsgiver = it.arbeidsgiver ?: DEFAULT_VALUE,
                selskapsnavn = it.selskapsnavn ?: DEFAULT_VALUE,
                produktbetegnelse = it.produktbetegnelse ?: DEFAULT_VALUE,
                kategori = Kategori.fromExternalValue(it.kategori).internalValue,
                underkategori = Underkategori.fromExternalValue(it.underkategori).internalValue,
                innskuddssaldo = it.innskuddssaldo ?: 0,
                naavaerendeAvtaltAarligInnskudd = it.naavaerendeAvtaltAarligInnskudd ?: 0,
                pensjonsbeholdningForventet = it.pensjonsbeholdningForventet ?: 0,
                pensjonsbeholdningNedreGrense = it.pensjonsbeholdningNedreGrense ?: 0,
                pensjonsbeholdningOvreGrense = it.pensjonsbeholdningOvreGrense ?: 0,
                avkastningsgaranti = it.avkastningsgaranti ?: false,
                beregningsmodell = Beregningsmodell.fromExternalValue(it.beregningsmodell).internalValue,
                startAar = it.startAlder ?: 0,
                sluttAar = it.sluttAlder,
                opplysningsdato = it.opplysningsdato ?: DEFAULT_VALUE,
                manglendeGraderingAarsak = it.aarsakManglendeGradering?.internalValue ?: ManglendeEksternGraderingAarsak.NONE,
                manglendeBeregningAarsak = it.aarsakIkkeBeregnet?.internalValue ?: ManglendeEksternBeregningAarsak.NONE,
                utbetalingsperioder = it.utbetalingsperioder?.map(::utbetalingsperiode) ?: emptyList()
            )
        }

    private fun utilgjengeligeSelskap(dto: EnvelopeDto) =
        dto.body?.pensjonsrettigheter?.utilgjengeligeInnretninger?.map {
            Selskap(
                navn = it.selskapsnavn ?: DEFAULT_VALUE,
                heltUtilgjengelig = it.heltUtilgjengelig ?: false,
                antallManglendeRettigheter = it.antallManglendeRettigheter ?: 0,
                kategori = Kategori.fromExternalValue(it.kategori).internalValue,
                feilkode = it.feilkode ?: ""
            )
        }

    private fun utbetalingsperiode(source: UtbetalingsperiodeDto) =
        Utbetalingsperiode(
            startAlder = Alder(source.startAlder!!, source.startMaaned!! - 1),
            sluttAlder = source.sluttAlder?.let { Alder(it, source.sluttMaaned!! - 1) },
            aarligUtbetalingForventet = source.aarligUtbetalingForventet ?: 0,
            aarligUtbetalingNedreGrense = source.aarligUtbetalingNedreGrense ?: 0,
            aarligUtbetalingOvreGrense = source.aarligUtbetalingOvreGrense ?: 0,
            grad = source.grad?.let { Uttaksgrad.from(it) } ?: DEFAULT_UTTAKSGRAD
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
