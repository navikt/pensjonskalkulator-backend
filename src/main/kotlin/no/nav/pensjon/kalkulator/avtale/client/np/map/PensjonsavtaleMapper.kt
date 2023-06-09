package no.nav.pensjon.kalkulator.avtale.client.np.map

import no.nav.pensjon.kalkulator.avtale.*
import no.nav.pensjon.kalkulator.avtale.client.np.dto.EnvelopeDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.FaultDetailDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.FaultDto
import no.nav.pensjon.kalkulator.avtale.client.np.dto.UtbetalingsperioderDto

object PensjonsavtaleMapper {

    fun fromDto(dto: EnvelopeDto) =
        Pensjonsavtaler(
            pensjonsavtale(dto)?.let(::listOf) ?: emptyOrFault(dto),
            utilgjengeligeSelskap(dto)?.let(::listOf) ?: emptyList()
        )

    private fun pensjonsavtale(dto: EnvelopeDto) =
        dto.body?.privatPensjonsrettigheter?.privatAlderRettigheter?.let {
            Pensjonsavtale(
                it.produktbetegnelse ?: "ukjent",
                it.kategori ?: "ukjent",
                it.startAlder ?: 0,
                it.sluttAlder,
                utbetalingsperiode(it.utbetalingsperioder!!)
            )
        }

    private fun utilgjengeligeSelskap(dto: EnvelopeDto) =
        dto.body?.privatPensjonsrettigheter?.utilgjengeligeSelskap?.let {
            Selskap(
                it.navn ?: "ukjent",
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
