package no.nav.pensjon.kalkulator.opptjening

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class BeholdningVelgerTest : ShouldSpec({

    should("velge beholdningen med seneste dato") {
        BeholdningVelger.velg(
            listOf(
                DatertBeholdning(
                    dato = LocalDate.of(2000, 1, 1),
                    beholdning = 1,
                    oppdateringsaarsak = Beholdningsoppdateringsaarsak.NY_OPPTJENING
                ),
                DatertBeholdning(
                    dato = LocalDate.of(2000, 3, 1),
                    beholdning = 3,
                    oppdateringsaarsak = Beholdningsoppdateringsaarsak.VEDTAK
                ),
                DatertBeholdning(
                    dato = LocalDate.of(2000, 2, 1),
                    beholdning = 2,
                    oppdateringsaarsak = Beholdningsoppdateringsaarsak.REGULERING
                )
            )
        )?.beholdning shouldBe 3
    }
})