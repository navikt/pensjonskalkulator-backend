package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.tjenestepensjon.*
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpAfpStatusType
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.dto.*

object TpTjenestepensjonMapper {

    private val log = KotlinLogging.logger {}

    fun fromDto(dto: TpApotekerDto): Boolean = dto.harLopendeForholdApotekerforeningen ?: false

    fun fromDto(dto: TpTjenestepensjonStatusDto): Boolean = dto.value ?: false

    fun fromDto(dto: TpTjenestepensjonDto): Tjenestepensjon =
        Tjenestepensjon(dto.forhold?.map(::forhold).orEmpty())

    fun fromDto(dto: FinnTjenestepensjonsforholdResponsDto): Tjenestepensjonsforhold =
        Tjenestepensjonsforhold(dto.forhold.orEmpty().map { it.ordning.navn })

    fun fromDto(response: TpAfpOffentligLivsvarigDetaljerDto?): AfpOffentligLivsvarigResult {
        if (response == null) {
            return AfpOffentligLivsvarigResult(
                afpInnvilget = null,
                virkningFom = null,
                maanedligBeloepListe = emptyList(),
                sistBenyttetGrunnbeloep = null
            )
        }

        val afpInnvilget = response.statusAfp == TpAfpStatusType.INNVILGET

        if (afpInnvilget && response.belopsListe.isEmpty()) {
            log.warn { "Livsvarig offentlig AFP er innvilget, men beløpsliste er tom. Dette kan indikere datakvalitetsproblem." }
        }

        return AfpOffentligLivsvarigResult(
            afpInnvilget,
            virkningFom = if (afpInnvilget) response.virkningsDato else null,
            response.belopsListe.map { MaanedligBeloep(it.fomDato, it.belop) },
            sistBenyttetGrunnbeloep = if (afpInnvilget) response.sistBenyttetG else null
        )
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
