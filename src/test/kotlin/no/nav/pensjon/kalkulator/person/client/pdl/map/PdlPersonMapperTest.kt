package no.nav.pensjon.kalkulator.person.client.pdl.map

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.common.exception.NotFoundException
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.pdl.dto.*
import java.time.LocalDate

class PdlPersonMapperTest : ShouldSpec({

    val foedselsdato: LocalDate = LocalDate.of(1963, 12, 31)

    should("map response DTO to domain object") {
        val dto = responseDto(
            fornavnListe = listOf("For-Navn"),
            foedselsdatoListe = listOf(foedselsdato),
            sivilstandListe = listOf("UGIFT")
        )

        val person = PdlPersonMapper.fromDto(dto)

        person.navn shouldBe "For-Navn"
        person.foedselsdato shouldBe foedselsdato
        person.harFoedselsdato shouldBe true
        person.sivilstand shouldBe Sivilstand.UGIFT
    }

    should("pick first fornavn") {
        val dto = responseDto(fornavnListe = listOf("Kari", "Ola"))
        PdlPersonMapper.fromDto(dto).navn shouldBe "Kari"
    }

    should("pick first foedselsdato") {
        val dto = responseDto(foedselsdatoListe = listOf(foedselsdato, LocalDate.of(1962, 1, 1)))
        PdlPersonMapper.fromDto(dto).foedselsdato shouldBe foedselsdato
    }

    should("pick first sivilstand") {
        val dto = responseDto(sivilstandListe = listOf("UGIFT", "SKILT"))
        PdlPersonMapper.fromDto(dto).sivilstand shouldBe Sivilstand.UGIFT
    }

    should("pick first adressebeskyttelsegradering") {
        val dto = responseDto(adressebeskyttelseListe = listOf("FORTROLIG", "STRENGT_FORTROLIG"))
        PdlPersonMapper.fromDto(dto).adressebeskyttelse shouldBe AdressebeskyttelseGradering.FORTROLIG
    }

    should("map DTO with null data to null") {
        shouldThrow<NotFoundException> {
            PdlPersonMapper.fromDto(PdlPersonResult(data = null, extensions = null, errors = null))
        }.message shouldBe "person"
    }

    should("map missing fornavn to empty string") {
        PdlPersonMapper.fromDto(responseDto(fornavnListe = emptyList())).navn shouldBe ""
    }

    should("fornavn and etternavn to properly cased navn") {
        val dto = responseDto(
            fornavnListe = listOf("FØR"),
            etternavn = "ETTERPÅ"
        )

        val person = PdlPersonMapper.fromDto(dto)

        person.navn shouldBe "Før Etterpå"
        person.fornavn shouldBe "Før"
    }

    should("map missing foedselsdato to minimum date") {
        val person = PdlPersonMapper.fromDto(responseDto(foedselsdatoListe = emptyList()))
        person.foedselsdato shouldBe LocalDate.MIN
        person.harFoedselsdato shouldBe false
    }

    should("map missing sivilstand to sivilstand null") {
        PdlPersonMapper.fromDto(responseDto(sivilstandListe = emptyList())).sivilstand shouldBe Sivilstand.UOPPGITT
    }

    should("map unknown sivilstand to sivilstand 'uoppgitt'") {
        val dto = responseDto(sivilstandListe = listOf("not known"))
        PdlPersonMapper.fromDto(dto).sivilstand shouldBe Sivilstand.UNKNOWN
    }
})

private fun responseDto(
    fornavnListe: List<String> = emptyList(),
    etternavn: String? = null,
    foedselsdatoListe: List<LocalDate> = emptyList(),
    sivilstandListe: List<String> = emptyList(),
    adressebeskyttelseListe: List<String> = emptyList()
) =
    PdlPersonResult(
        data = PdlPersonEnvelope(
            PdlPerson(
                navn = fornavnListe.map {
                    PdlNavn(fornavn = it, mellomnavn = null, etternavn)
                },
                foedselsdato = foedselsdatoListe.map(::foedselsdato),
                sivilstand = sivilstandListe.map(::PdlSivilstand),
                adressebeskyttelse = adressebeskyttelseListe.map(::PdlAdressebeskyttelse)
            )
        ),
        extensions = extensions(),
        errors = errors()
    )

private fun foedselsdato(dato: LocalDate) =
    PdlFoedselsdato(foedselsdato = PdlDate(value = dato))

private fun extensions() =
    PdlExtensions(
        warnings = listOf(
            PdlWarning(
                query = "query",
                id = "id",
                code = "code",
                message = "message",
                details = "details"
            )
        )
    )

private fun errors() =
    listOf(
        PdlError(message = "feil1"),
        PdlError(message = "feil2")
    )
