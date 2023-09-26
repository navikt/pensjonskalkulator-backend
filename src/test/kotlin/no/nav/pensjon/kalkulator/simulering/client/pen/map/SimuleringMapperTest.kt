package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class SimuleringMapperTest {

    @Test
    fun `toDto maps sivilstand, simuleringstype to PEN values`() {
        with(SimuleringMapper.toDto(impersonalSpec(), personalSpec())) {
            assertEquals("UGIF", sivilstand)
            assertEquals("ALDER", simuleringstype)
        }
    }

    private companion object {
        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                uttaksgrad = Uttaksgrad.HUNDRE_PROSENT,
                foersteUttaksalder = Alder(67, 1),
                foedselsdato = LocalDate.of(1963, 1, 1),
                epsHarInntektOver2G = true
            )

        private fun personalSpec() =
            PersonalSimuleringSpec(
                pid = pid,
                forventetInntekt = 100_000,
                sivilstand = Sivilstand.UGIFT
            )
    }
}
