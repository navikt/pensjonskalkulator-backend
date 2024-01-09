package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.DateFactory
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class PenSimuleringMapperTest {

    @Test
    fun `toDto maps sivilstand, simuleringstype, dato to PEN values`() {
        with(PenSimuleringMapper.toDto(impersonalSpec(), personalSpec())) {
            assertEquals("UGIF", sivilstand)
            assertEquals("ALDER", simuleringstype)
            assertEquals(DateFactory.date(2030, Calendar.MARCH), forsteUttaksdato)
            assertEquals("P_80", uttaksgrad)
            assertEquals(DateFactory.date(2032, Calendar.JANUARY), heltUttakDato)
            assertEquals(12_000, inntektUnderGradertUttak)
        }
    }

    private companion object {
        private val foedselDato = LocalDate.of(1963, 1, 1)

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                foersteUttakAlder = Alder(67, 1),
                foedselDato = foedselDato,
                epsHarInntektOver2G = true,
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.AATTI_PROSENT,
                    heltUttakAlder = Alder(68, 11),
                    inntektUnderGradertUttak = 12_000,
                    foedselDato = foedselDato
                )
            )

        private fun personalSpec() =
            PersonalSimuleringSpec(
                pid = pid,
                forventetInntekt = 100_000,
                sivilstand = Sivilstand.UGIFT
            )
    }
}
