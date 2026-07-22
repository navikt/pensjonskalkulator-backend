package no.nav.pensjon.kalkulator.person.api.intern.v1.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.foedselsdato
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.AdressebeskyttelseGradering
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus

class PersonMapperTest : ShouldSpec({

    should("map a domain-represented 'person' to a transferable representation") {
        PersonMapper.transferable(
            Person(
                navn = "Fornavn1 Etternavn1",
                fornavn = "Fornavn1", // not mapped
                foedselsdato = foedselsdato,
                pensjoneringAldre = Aldersgrenser(
                    aarskull = 1963, // not mapped
                    nedreAlder = Alder(aar = 62, maaneder = 2),
                    normalder = Alder(aar = 67, maaneder = 2),
                    oevreAlder = Alder(aar = 76, maaneder = 2),
                    verdiStatus = VerdiStatus.PROGNOSE // not mapped
                ),
                sivilstand = Sivilstand.ENKE_ELLER_ENKEMANN,
                sivilstatus = Sivilstatus.SAMBOER,
                adressebeskyttelse = AdressebeskyttelseGradering.FORTROLIG // not mapped
            )
        ) shouldBe
                PersonInternV1Person(
                    navn = "Fornavn1 Etternavn1",
                    foedselsdato = foedselsdato,
                    sivilstand = PersonInternV1Sivilstand.ENKE_ELLER_ENKEMANN,
                    sivilstatus = PersonInternV1Sivilstatus.SAMBOER,
                    pensjoneringAldre = PersonInternV1Pensjonsaldre(
                        normertPensjoneringsalder = PersonInternV1Alder(aar = 67, maaneder = 2),
                        nedreAldersgrense = PersonInternV1Alder(aar = 62, maaneder = 2),
                        oevreAldersgrense = PersonInternV1Alder(aar = 76, maaneder = 2),
                    )
                )
    }
})