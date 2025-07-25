package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.Sivilstatus
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.*
import no.nav.pensjon.kalkulator.avtale.client.np.v3.map.NorskPensjonSluttAlderMapper.sluttAar
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.time.DateUtil.MAANEDER_PER_AAR

object NorskPensjonPensjonsavtaleMapper {

    /**
     * Norsk Pensjon angir måned 1..12, vi angir antall måneder 0..11 => forskyvning 1
     */
    private const val STARTMAANED_FORSKYVNING = 1

    /**
     *  Norsk Pensjon regner "til", vi regner "til og med" => forskyvning 1
     *  Norsk Pensjon angir måned 1..12, vi angir antall måneder 0..11 => forskyvning 1
     *  Total forskyvning: 2
     */
    const val SLUTTMAANED_FORSKYVNING = 2

    /**
     * NB: Norsk Pensjon's documentation says that 14 represents "livsvarig".
     * However, using 14 makes Norsk Pensjon return error "No signature in message!".
     * As a workoround the value 13 is used instead (although this represents "13 years" instead of "livsvarig").
     */
    private const val ANTALL_AAR_REPRESENTING_LIVSVARIG = 13
    private const val DEFAULT_VALUE = "ukjent"
    private const val DEFAULT_HAR_EPS_PENSJON = true // Norsk Pensjon default
    private const val DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G = true // Norsk Pensjon default

    fun fromDto(dto: EnvelopeDto) =
        Pensjonsavtaler(
            avtaler = pensjonsavtaler(dto) ?: emptyOrFault(dto),
            utilgjengeligeSelskap = utilgjengeligeSelskap(dto) ?: emptyList()
        )

    fun toDto(spec: PensjonsavtaleSpec, pid: Pid) =
        NorskPensjonPensjonsavtaleSpecDto(
            pid = pid,
            aarligInntektFoerUttak = spec.aarligInntektFoerUttak,
            uttaksperioder = spec.uttaksperioder.map(::uttaksperiodeSpecDto),
            antallInntektsaarEtterUttak = antallInntektAarUnderHeltUttak(spec.uttaksperioder),
            harAfp = false, // avoids Norsk Pensjon calling Nav's AFP simulation
            harEpsPensjon = spec.harEpsPensjon ?: DEFAULT_HAR_EPS_PENSJON,
            harEpsPensjonsgivendeInntektOver2G = spec.harEpsPensjonsgivendeInntektOver2G
                ?: DEFAULT_HAR_EPS_PENSJONSGIVENDE_INNTEKT_OVER_2G,
            antallAarIUtlandetEtter16 = 0, // only relevant if oenskesSimuleringAvFolketrygd = true
            sivilstatus = Sivilstatus.fromInternalValue(spec.sivilstand),
            oenskesSimuleringAvFolketrygd = false
        )

    private fun antallInntektAarUnderHeltUttak(perioder: List<UttaksperiodeSpec>): Int {
        val heltUttakPeriode = perioder.firstOrNull { it.grad == Uttaksgrad.HUNDRE_PROSENT } ?: return 0

        return if (heltUttakPeriode.aarligInntekt == null) 0
        else heltUttakPeriode.aarligInntekt.tomAlder
            ?.let { (it.aar - heltUttakPeriode.startAlder.aar).coerceAtMost(ANTALL_AAR_REPRESENTING_LIVSVARIG) }
            ?: ANTALL_AAR_REPRESENTING_LIVSVARIG
    }

    private fun uttaksperiodeSpecDto(spec: UttaksperiodeSpec) =
        NorskPensjonUttaksperiodeSpecDto(
            startAlder = NorskPensjonAlderDto(spec.startAlder.aar, spec.startAlder.maaneder + STARTMAANED_FORSKYVNING),
            grad = spec.grad,
            aarligInntekt = spec.aarligInntekt?.aarligBeloep ?: 0
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
                sluttAar = sluttAar(it.sluttAlder, it.utbetalingsperioder),
                opplysningsdato = it.opplysningsdato ?: DEFAULT_VALUE,
                manglendeGraderingAarsak = AarsakManglendeGradering.fromExternalValue(it.aarsakManglendeGradering).internalValue,
                manglendeBeregningAarsak = AarsakIkkeBeregnet.fromExternalValue(it.aarsakIkkeBeregnet).internalValue,
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
            startAlder = Alder(source.startAlder, source.startMaaned - STARTMAANED_FORSKYVNING),
            sluttAlder = source.sluttAlder?.let { sluttalder(it, source.sluttMaaned!!) },
            aarligUtbetalingForventet = source.aarligUtbetalingForventet ?: 0,
            aarligUtbetalingNedreGrense = source.aarligUtbetalingNedreGrense ?: 0,
            aarligUtbetalingOvreGrense = source.aarligUtbetalingOvreGrense ?: 0,
            grad = source.grad.let { Uttaksgrad.from(it) }
        )

    private fun sluttalder(norskPensjonSluttAlder: Int, norskPensjonSluttMaaned: Int): Alder {
        val maaneder = norskPensjonSluttMaaned - SLUTTMAANED_FORSKYVNING

        return if (maaneder < 0)
            Alder(aar = norskPensjonSluttAlder - 1, maaneder = maaneder + MAANEDER_PER_AAR)
        else
            Alder(aar = norskPensjonSluttAlder, maaneder = maaneder)
    }

    private fun emptyOrFault(dto: EnvelopeDto) =
        dto.body?.fault?.run { throw PensjonsavtaleException(faultToString(this)) }
            ?: emptyList<Pensjonsavtale>()

    private fun nullableDetailToString(detail: FaultDetailDto?) = detail?.let(::detailToString) ?: "no detail"

    private fun detailToString(detail: FaultDetailDto) =
        detail.let {
            "Transaction: ${it.transactionId} | Global transaction: ${it.globalTransactionId}"
        }
}
