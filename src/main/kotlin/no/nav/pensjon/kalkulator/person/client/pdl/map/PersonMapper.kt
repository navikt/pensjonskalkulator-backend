package no.nav.pensjon.kalkulator.person.client.pdl.map

import no.nav.pensjon.kalkulator.person.Land
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import java.time.LocalDate

object PersonMapper {

    fun fromDto(dto: PersonResponseDto): Person =
        dto.data?.hentPerson?.let { person(it) } ?: emptyPerson()

    private fun person(dto: PersonDto) =
        Person(
            fromDto(dto.navn),
            fromDto(dto.foedsel),
            fromDto(dto.statsborgerskap),
            fromDto(dto.sivilstand)
        )

    private fun fromDto(dto: List<NavnDto>) =
        dto.firstOrNull()?.fornavn ?: "NN"

    private fun fromDto(dto: List<FoedselDto>) =
        dto.firstOrNull()?.foedselsdato ?: LocalDate.MIN

    private fun fromDto(dto: List<StatsborgerskapDto>) =
        dto.firstOrNull()?.land?.let { Land.forCode(it) } ?: Land.OTHER

    private fun fromDto(dto: List<SivilstandDto>) =
        dto.firstOrNull()?.type?.let { Sivilstand.forPdlCode(it) } ?: Sivilstand.UOPPGITT

    private fun emptyPerson() = Person("", LocalDate.MIN, Land.OTHER, Sivilstand.UOPPGITT)
}
