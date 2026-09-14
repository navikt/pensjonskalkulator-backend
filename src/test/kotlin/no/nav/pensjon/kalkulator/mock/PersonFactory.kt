package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.normalder.Aldersgrenser
import no.nav.pensjon.kalkulator.normalder.VerdiStatus
import no.nav.pensjon.kalkulator.person.Navn
import no.nav.pensjon.kalkulator.person.Person
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.Sivilstatus
import java.time.LocalDate

object PersonFactory {

    private val defaultFoedselsdato = LocalDate.of(1963, 12, 31)

    val pid = Pid("12906498357") // synthetic fødselsnummer
    val foedselsdato = defaultFoedselsdato

    fun person(sivilstand: Sivilstand = Sivilstand.UOPPGITT, foedselsdato: LocalDate = defaultFoedselsdato) =
        Person(
            navn = Navn(fornavn = "Fornavn1", etternavn = "Etternavn1"),
            foedselsdato,
            sivilstand = sivilstand
        )

    fun personWithPensjoneringAldre() =
        Person(
            navn = Navn(fornavn = "Fornavn1", etternavn = "Etternavn1"),
            foedselsdato = foedselsdato,
            sivilstand = Sivilstand.SKILT,
            sivilstatus = Sivilstatus.SAMBOER,
            pensjoneringAldre = Aldersgrenser(
                aarskull = 1963,
                nedreAlder = Alder(aar = 62, maaneder = 1),
                normalder = Alder(aar = 67, maaneder = 1),
                oevreAlder = Alder(aar = 75, maaneder = 1),
                verdiStatus = VerdiStatus.PROGNOSE
            )
        )
}