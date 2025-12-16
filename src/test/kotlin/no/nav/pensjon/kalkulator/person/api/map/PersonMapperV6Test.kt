package no.nav.pensjon.kalkulator.person.api.map

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.foedselsdato
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.api.dto.PersonAlderV6
import no.nav.pensjon.kalkulator.person.api.dto.PersonPensjonsaldreV6
import no.nav.pensjon.kalkulator.person.api.dto.PersonResultV6
import no.nav.pensjon.kalkulator.person.api.dto.PersonSivilstandV6
import org.junit.jupiter.api.Test

class PersonMapperV6Test {

    @Test
    fun `dtoV6 person to data transfer object version 4`() {
        val dto: PersonResultV6 = PersonMapperV6.dtoV6(
            Person(
                navn = "Fornavn1 Etternavn1",
                fornavn = "Fornavn1",
                foedselsdato = foedselsdato,
                pensjoneringAldre = Aldersgrenser(
                    aarskull = 1963,
                    nedreAlder = Alder(aar = 62, maaneder = 2),
                    normalder = Alder(aar = 67, maaneder = 2),
                    oevreAlder = Alder(aar = 76, maaneder = 2),
                    verdiStatus = VerdiStatus.PROGNOSE
                ),
                sivilstand = Sivilstand.GIFT,
                adressebeskyttelse = AdressebeskyttelseGradering.FORTROLIG
            )
        )

        dto shouldBe PersonResultV6(
            navn = "Fornavn1 Etternavn1",
            fornavn = "Fornavn1",
            foedselsdato = foedselsdato,
            pensjoneringAldre = PersonPensjonsaldreV6(
                normertPensjoneringsalder = PersonAlderV6(67, 2),
                nedreAldersgrense = PersonAlderV6(62, 2),
                oevreAldersgrense = PersonAlderV6(76, 2),
            ),
            sivilstand = PersonSivilstandV6.GIFT
        )
    }
}
