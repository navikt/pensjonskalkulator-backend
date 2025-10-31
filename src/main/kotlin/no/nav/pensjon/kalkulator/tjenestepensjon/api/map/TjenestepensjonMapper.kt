package no.nav.pensjon.kalkulator.tjenestepensjon.api.map

import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.AfpOffentligLivsvarigDto
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.MedlemskapITjenestepensjonsordningDto
import no.nav.pensjon.kalkulator.tjenestepensjon.api.dto.TjenestepensjonsforholdDto

object TjenestepensjonMapper {
    fun toDto(harForhold: Boolean) = TjenestepensjonsforholdDto(harForhold)
    fun toDto(tpLeverandoerer: List<String>) = MedlemskapITjenestepensjonsordningDto(tpLeverandoerer)
    fun toDto(result: AfpOffentligLivsvarigResult) = AfpOffentligLivsvarigDto(
        afpStatus = result.afpStatus,
        beloep = result.beloep
    )
}
