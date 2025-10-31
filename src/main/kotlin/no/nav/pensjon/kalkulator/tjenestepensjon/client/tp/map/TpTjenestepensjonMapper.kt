package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import no.nav.pensjon.kalkulator.tjenestepensjon.*
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpAfpStatusType
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.*

object TpTjenestepensjonMapper {

    fun fromDto(dto: TpApotekerDto): Boolean = dto.harLopendeForholdApotekerforeningen ?: false

    fun fromDto(dto: TpTjenestepensjonStatusDto): Boolean = dto.value ?: false

    fun fromDto(dto: TpTjenestepensjonDto): Tjenestepensjon =
        Tjenestepensjon(dto.forhold?.map(::forhold).orEmpty())

    fun fromDto(dto: FinnTjenestepensjonsforholdResponsDto): Tjenestepensjonsforhold =
        Tjenestepensjonsforhold(dto.forhold.orEmpty().map { it.ordning.navn })

    fun fromDto(response: TpAfpOffentligLivsvarigDetaljerDto?): AfpOffentligLivsvarigResult {
        if (response == null) {
            return AfpOffentligLivsvarigResult(null, null)
        }

        // Map AFP status to boolean (true if INNVILGET)
        val afpStatus = when (response.statusAfp) {
            TpAfpStatusType.INNVILGET -> true
            TpAfpStatusType.UKJENT, TpAfpStatusType.IKKE_SOKT, TpAfpStatusType.SOKT, TpAfpStatusType.AVSLAG -> false
        }

        val beloep = response.belopsListe.lastOrNull()?.belop

        return AfpOffentligLivsvarigResult(afpStatus, beloep)
    }

    private fun forhold(dto: TpForholdDto): Forhold =
        Forhold(
            ordning = dto.ordning ?: "",
            ytelser = dto.ytelser?.map(::ytelse).orEmpty(),
            datoSistOpptjening = dto.datoSistOpptjening
        )

    private fun ytelse(dto: TpYtelseDto): Ytelse =
        Ytelse(
            type = dto.type ?: "",
            datoInnmeldtYtelseFom = dto.datoInnmeldtYtelseFom,
            datoYtelseIverksattFom = dto.datoYtelseIverksattFom,
            datoYtelseIverksattTom = dto.datoYtelseIverksattTom
        )
}
