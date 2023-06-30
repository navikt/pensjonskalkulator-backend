package no.nav.pensjon.kalkulator.avtale.client.np.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.client.np.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.FaultDetailDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.FaultDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.UtbetalingsperioderDto

object PensjonsavtaleMapper {

    private const val DEFAULT_VALUE = "ukjent"

    fun fromDto(dto: EnvelopeDto) =
        Pensjonsavtaler(
            pensjonsavtaler(dto) ?: emptyOrFault(dto),
            utilgjengeligeSelskap(dto) ?: emptyList()
        )

    private fun pensjonsavtaler(dto: EnvelopeDto) =
        dto.body?.privatPensjonsrettigheter?.privatAlderRettigheter?.map {
            Pensjonsavtale(
                it.produktbetegnelse ?: DEFAULT_VALUE,
                it.kategori ?: DEFAULT_VALUE,
                it.startAlder ?: 0,
                it.sluttAlder,
                it.utbetalingsperioder?.map(::utbetalingsperiode) ?: emptyList()
            )
        }

    private fun utilgjengeligeSelskap(dto: EnvelopeDto) =
        dto.body?.privatPensjonsrettigheter?.utilgjengeligeSelskap?.map {
            Selskap(
                it.navn ?: DEFAULT_VALUE,
                it.heltUtilgjengelig ?: false
            )
        }

    private fun utbetalingsperiode(source: UtbetalingsperioderDto) =
        Utbetalingsperiode(
            Alder(source.startAlder!!, source.startMaaned!!),
            source.sluttAlder?.let { Alder(it, source.sluttMaaned!!) },
            source.aarligUtbetaling ?: 0,
            source.grad ?: 0
        )

    private fun emptyOrFault(dto: EnvelopeDto) =
        dto.body?.fault?.run { throw RuntimeException(faultToString(this)) }
            ?: emptyList<Pensjonsavtale>()

    private fun faultToString(fault: FaultDto) =
        fault.let {
            "Code: ${it.faultcode} | String: ${it.faultstring} | Actor: ${it.faultactor}" +
                    " | Detail: { ${nullableDetailToString(it.detail)} }"
        }

    private fun nullableDetailToString(detail: FaultDetailDto?) = detail?.let(::detailToString) ?: "no detail"

    private fun detailToString(detail: FaultDetailDto) =
        detail.let {
            "Transaction: ${it.transactionId} | Global transaction: ${it.globalTransactionId}"
        }
}
