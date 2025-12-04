package no.nav.pensjon.kalkulator.person.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.foedselsdato
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV5
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV5
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV5
import no.nav.pensjon.kalkulator.person.api.dto.PersonSivilstandV5
import org.junit.jupiter.api.Test

class PersonMapperV5Test {

    @Test
    fun `dtoV5 person to data transfer object version 5`() {
        PersonMapperV5.dtoV5(
            Person(
                navn = "Fornavn1 Etternavn1",
                fornavn = "Fornavn1",
                foedselsdato = foedselsdato,
                pensjoneringAldre = Aldersgrenser(
                    aarskull = 1963,
                    nedreAlder = Alder(aar = 62, maaneder = 2),
                    normalder = Alder(aar = 67, maaneder = 2),
                    oevreAlder = Alder(aar = 75, maaneder = 2),
                    verdiStatus = VerdiStatus.PROGNOSE
                ),
                sivilstand = Sivilstand.GIFT,
                adressebeskyttelse = AdressebeskyttelseGradering.FORTROLIG
            )
        ) shouldBe PersonResultV5(
            navn = "Fornavn1",
            foedselsdato = foedselsdato,
            pensjoneringAldre = PersonPensjoneringAldreV5(
                normertPensjoneringsalder = PersonAlderV5(67, 2),
                nedreAldersgrense = PersonAlderV5(62, 2),
                oevreAldersgrense = PersonAlderV5(75, 2),
            ),
            sivilstand = PersonSivilstandV5.GIFT
        )
    }
}
