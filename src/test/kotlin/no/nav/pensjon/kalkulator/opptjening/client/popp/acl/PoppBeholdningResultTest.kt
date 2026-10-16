package no.nav.pensjon.kalkulator.opptjening.client.popp.acl

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.mock.DateFactory.date
import no.nav.pensjon.kalkulator.opptjening.Beholdningsoppdateringsaarsak
import no.nav.pensjon.kalkulator.opptjening.DatertBeholdning
import java.time.LocalDate
import java.time.Month
import java.util.Calendar

class PoppBeholdningResultTest : ShouldSpec({

    should("map data transfer object to domain object") {
        PoppBeholdningResult(
            beholdninger = listOf(
                PoppBeholdning(
                    belop = 123.8,
                    oppdateringArsak = "VEDTAK",
                    fomDato = date(year = 2019, month = Calendar.NOVEMBER)
                )
            )
        ).toInternalValue() shouldBe listOf(
            DatertBeholdning(
                dato = LocalDate.of(2019, Month.NOVEMBER, 1),
                beholdning = 123,
                oppdateringsaarsak = Beholdningsoppdateringsaarsak.VEDTAK
            )
        )
    }
})