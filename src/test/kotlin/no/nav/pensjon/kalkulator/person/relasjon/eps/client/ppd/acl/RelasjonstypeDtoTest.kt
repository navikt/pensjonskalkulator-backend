package no.nav.pensjon.kalkulator.person.relasjon.eps.client.ppd.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.person.Sivilstatus

class RelasjonstypeDtoTest : ShouldSpec({

    context("fromSivilstatus") {
        should("mappe 'gift', 'separert', 'skilt', 'enke/enkemann' til 'ektefelle'") {
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.GIFT) shouldBe RelasjonstypeDto.EKTEFELLE
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.SEPARERT) shouldBe RelasjonstypeDto.EKTEFELLE
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.SKILT) shouldBe RelasjonstypeDto.EKTEFELLE
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.ENKE_ELLER_ENKEMANN) shouldBe RelasjonstypeDto.EKTEFELLE
        }

        should("mappe 'registrert partner', 'separert partner', 'skilt partner', 'gjenlevende partner' til 'registrert partner'") {
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.REGISTRERT_PARTNER) shouldBe RelasjonstypeDto.REGISTRERT_PARTNER
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.SEPARERT_PARTNER) shouldBe RelasjonstypeDto.REGISTRERT_PARTNER
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.SKILT_PARTNER) shouldBe RelasjonstypeDto.REGISTRERT_PARTNER
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.GJENLEVENDE_PARTNER) shouldBe RelasjonstypeDto.REGISTRERT_PARTNER
        }

        should("mappe 'samboer', 'ugift', 'uoppgitt', 'unknown' til 'samboer'") {
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.SAMBOER) shouldBe RelasjonstypeDto.SAMBOER
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.UGIFT) shouldBe RelasjonstypeDto.SAMBOER
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.UOPPGITT) shouldBe RelasjonstypeDto.SAMBOER
            RelasjonstypeDto.fromSivilstatus(Sivilstatus.UNKNOWN) shouldBe RelasjonstypeDto.SAMBOER
        }

        should("mappe udefinert til 'samboer'") {
            RelasjonstypeDto.fromSivilstatus(null) shouldBe RelasjonstypeDto.SAMBOER
        }
    }
})