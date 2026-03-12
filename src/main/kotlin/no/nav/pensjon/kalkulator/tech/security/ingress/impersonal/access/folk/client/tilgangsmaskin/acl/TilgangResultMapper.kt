package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl

import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.AvvisningAarsak
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.TilgangResult

object TilgangResultMapper {

    fun fromDto(source: TilgangResultDto): TilgangResult =
        when (source) {
            is TilgangResultDto.Innvilget -> TilgangResult(
                innvilget = true,
                avvisningAarsak = null,
                begrunnelse = null,
                traceId = null
            )
            is TilgangResultDto.Avvist -> TilgangResult(
                innvilget = false,
                avvisningAarsak = avvisningAarsak(source.detail.title),
                begrunnelse = source.detail.begrunnelse,
                traceId = source.detail.traceId
            )
        }

    private fun avvisningAarsak(kode: AvvisningsKodeDto): AvvisningAarsak =
        when (kode) {
            AvvisningsKodeDto.AVVIST_STRENGT_FORTROLIG_ADRESSE -> AvvisningAarsak.STRENGT_FORTROLIG_ADRESSE
            AvvisningsKodeDto.AVVIST_STRENGT_FORTROLIG_UTLAND -> AvvisningAarsak.STRENGT_FORTROLIG_UTLAND
            AvvisningsKodeDto.AVVIST_AVDOED -> AvvisningAarsak.AVDOED
            AvvisningsKodeDto.AVVIST_PERSON_UTLAND -> AvvisningAarsak.PERSON_UTLAND
            AvvisningsKodeDto.AVVIST_SKJERMING -> AvvisningAarsak.SKJERMING
            AvvisningsKodeDto.AVVIST_FORTROLIG_ADRESSE -> AvvisningAarsak.FORTROLIG_ADRESSE
            AvvisningsKodeDto.AVVIST_UKJENT_BOSTED -> AvvisningAarsak.UKJENT_BOSTED
            AvvisningsKodeDto.AVVIST_GEOGRAFISK -> AvvisningAarsak.GEOGRAFISK
            AvvisningsKodeDto.AVVIST_HABILITET -> AvvisningAarsak.HABILITET
        }
}