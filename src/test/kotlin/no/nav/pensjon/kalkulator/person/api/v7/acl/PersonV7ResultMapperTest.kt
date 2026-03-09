package no.nav.pensjon.kalkulator.person.api.v7.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.foedselsdato
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand

class PersonV7ResultMapperTest : ShouldSpec({

    should("map person to data transfer object version 7") {
        PersonResultMapper.toDto(
            source = Person(
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
        ) shouldBe PersonV7Result(
            navn = "Fornavn1 Etternavn1",
            fornavn = "Fornavn1",
            foedselsdato = foedselsdato,
            sivilstatus = PersonV7Sivilstatus.GIFT,
            pensjoneringAldre = PersonV7Pensjonsaldre(
                normertPensjoneringsalder = PersonV7Alder(aar = 67, maaneder = 2),
                nedreAldersgrense = PersonV7Alder(aar = 62, maaneder = 2),
                oevreAldersgrense = PersonV7Alder(aar = 76, maaneder = 2)
            )
        )
    }
})
