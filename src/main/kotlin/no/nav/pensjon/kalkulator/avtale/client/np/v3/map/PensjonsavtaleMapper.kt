package no.nav.pensjon.kalkulator.avtale.client.np.v3.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.FaultDetailDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.FaultDto
import no.nav.pensjon.kalkulator.avtale.client.np.v3.dto.UtbetalingsperiodeDto

object PensjonsavtaleMapper {

    private const val DEFAULT_VALUE = "ukjent"
    private val DEFAULT_UTTAKSGRAD = Uttaksgrad.HUNDRE_PROSENT

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
            Alder(source.startAlder!!, source.startMaaned!!),
            source.sluttAlder?.let { Alder(it, source.sluttMaaned!!) },
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
