package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.folk.client.tilgangsmaskin.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.access.AvvisningAarsak

class AvvisningsKodeDtoTest : ShouldSpec({

    context("internalValue") {
        should("map external value from Tilgangsmaskinen to internal value") {
            AvvisningsKodeDto.internalValue("AVVIST_STRENGT_FORTROLIG_ADRESSE") shouldBe AvvisningAarsak.STRENGT_FORTROLIG_ADRESSE
            AvvisningsKodeDto.internalValue("AVVIST_STRENGT_FORTROLIG_UTLAND") shouldBe AvvisningAarsak.STRENGT_FORTROLIG_UTLAND
            AvvisningsKodeDto.internalValue("AVVIST_AVDØD") shouldBe AvvisningAarsak.AVDOED
            AvvisningsKodeDto.internalValue("AVVIST_VERGEMÅL") shouldBe AvvisningAarsak.VERGEMAAL
            AvvisningsKodeDto.internalValue("AVVIST_PERSON_UTLAND") shouldBe AvvisningAarsak.PERSON_UTLAND
            AvvisningsKodeDto.internalValue("AVVIST_SKJERMING") shouldBe AvvisningAarsak.SKJERMING
            AvvisningsKodeDto.internalValue("AVVIST_FORTROLIG_ADRESSE") shouldBe AvvisningAarsak.FORTROLIG_ADRESSE
            AvvisningsKodeDto.internalValue("AVVIST_UKJENT_BOSTED") shouldBe AvvisningAarsak.UKJENT_BOSTED
            AvvisningsKodeDto.internalValue("AVVIST_GEOGRAFISK") shouldBe AvvisningAarsak.GEOGRAFISK
            AvvisningsKodeDto.internalValue("AVVIST_HABILITET") shouldBe AvvisningAarsak.HABILITET
        }

        should("map unknown or missing external value to 'unknown'") {
            AvvisningsKodeDto.internalValue("?") shouldBe AvvisningAarsak.UNKNOWN
            AvvisningsKodeDto.internalValue("") shouldBe AvvisningAarsak.UNKNOWN
            AvvisningsKodeDto.internalValue(null) shouldBe AvvisningAarsak.UNKNOWN
        }

        should("be case-insensitive") {
            AvvisningsKodeDto.internalValue("Avvist_vergemål") shouldBe AvvisningAarsak.VERGEMAAL
        }
    }
})