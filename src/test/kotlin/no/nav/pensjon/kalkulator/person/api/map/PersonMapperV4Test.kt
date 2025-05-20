package no.nav.pensjon.kalkulator.person.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.foedselsdato
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjoneringAldreV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV4
import no.nav.pensjon.kalkulator.person.api.dto.PersonSivilstandV4
import org.junit.jupiter.api.Test

class PersonMapperV4Test {

    @Test
    fun `dtoV4 person to data transfer object version 4`() {
        val dto: PersonResultV4 = PersonMapperV4.dtoV4(
            Person(
                navn = "Fornavn1",
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
        )

        dto shouldBe PersonResultV4(
            navn = "Fornavn1",
            foedselsdato = foedselsdato,
            pensjoneringAldre = PersonPensjoneringAldreV4(
                normertPensjoneringsalder = PersonAlderV4(67, 2),
                nedreAldersgrense = PersonAlderV4(62, 2)
            ),
            sivilstand = PersonSivilstandV4.GIFT
        )
    }
}
