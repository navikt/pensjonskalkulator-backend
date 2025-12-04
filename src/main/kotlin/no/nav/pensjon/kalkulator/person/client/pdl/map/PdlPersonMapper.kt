package no.nav.pensjon.kalkulator.person.client.pdl.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.NavnFormatter.formatNavn
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import java.time.LocalDate

object PdlPersonMapper {
    private val log = KotlinLogging.logger {}

    fun fromDto(dto: PdlPersonResult): Person =
        dto.data?.hentPerson?.let(::person)
            ?: throw NotFoundException("person").also { logError(dto) }

    private fun person(dto: PdlPerson) =
        Person(
            navn = dto.navn.orEmpty().let(::fromDto) ?: "",
            fornavn = dto.navn?.firstOrNull()?.fornavn ?: "",
            foedselsdato = dto.foedselsdato.orEmpty().let(::fromDto) ?: LocalDate.MIN,
            sivilstand = dto.sivilstand.orEmpty().let(::fromDto),
            adressebeskyttelse = dto.adressebeskyttelse.orEmpty().let(::fromDto)
        )

    private fun fromDto(dto: List<PdlAdressebeskyttelse>): AdressebeskyttelseGradering =
        PdlAdressebeskyttelseGradering.fromExternalValue(dto.firstOrNull()?.gradering).internalValue

    private fun fromDto(dto: List<PdlFoedselsdato>): LocalDate? = dto.firstOrNull()?.foedselsdato?.value

    private fun fromDto(dto: List<PdlNavn>): String? =
        dto.firstOrNull()?.let { formatNavn(it.fornavn, it.mellomnavn, it.etternavn) }

    private fun fromDto(dto: List<PdlSivilstand>): Sivilstand =
        PdlSivilstandType.fromExternalValue(dto.firstOrNull()?.type).internalValue

    private fun logError(dto: PdlPersonResult) {
        dto.errors?.firstOrNull()?.message?.let {
            log.info { "PDL error message: $it" }
        }
    }
}
