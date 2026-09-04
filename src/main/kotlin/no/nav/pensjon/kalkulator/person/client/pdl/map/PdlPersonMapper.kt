package no.nav.pensjon.kalkulator.person.client.pdl.map

import mu.KotlinLogging
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Navn
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
            navn = dto.navn.orEmpty().let(::navn) ?: Navn.ukjent,
            foedselsdato = dto.foedselsdato.orEmpty().let(::foedselsdato) ?: LocalDate.MIN,
            sivilstand = dto.sivilstand.orEmpty().let(::fromDto),
            adressebeskyttelse = dto.adressebeskyttelse.orEmpty().let(::adressebeskyttelse)
        )

    private fun adressebeskyttelse(dto: List<PdlAdressebeskyttelse>): AdressebeskyttelseGradering =
        PdlAdressebeskyttelseGradering.fromExternalValue(dto.firstOrNull()?.gradering).internalValue

    private fun foedselsdato(dto: List<PdlFoedselsdato>): LocalDate? =
        dto.firstOrNull()?.foedselsdato?.value

    private fun navn(dto: List<PdlNavn>): Navn? =
        dto.firstOrNull()?.let { Navn(it.fornavn, it.mellomnavn, it.etternavn) }

    private fun fromDto(sivilstand: List<PdlSivilstand>): Sivilstand =
        PdlSivilstandType.fromExternalValue(sivilstand.firstOrNull()?.type).internalValue

    private fun logError(dto: PdlPersonResult) {
        dto.errors?.firstOrNull()?.message?.let {
            log.info { "PDL error message: $it" }
        }
    }
}