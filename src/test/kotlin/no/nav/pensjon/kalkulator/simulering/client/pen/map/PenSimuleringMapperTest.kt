package no.nav.pensjon.kalkulator.simulering.client.pen.map

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.GradertUttak
import no.nav.pensjon.kalkulator.general.HeltUttak
import no.nav.pensjon.kalkulator.general.Uttaksgrad
import no.nav.pensjon.kalkulator.mock.DateFactory
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.ImpersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.PersonalSimuleringSpec
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.simulering.client.pen.dto.SimuleringEgressSpecDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class PenSimuleringMapperTest {

    @Test
    fun `toDto maps domain object to PEN-specific data transfer object`() {
        val dto: SimuleringEgressSpecDto = PenSimuleringMapper.toDto(impersonalSpec(), personalSpec())

        with(dto) {
            assertEquals("ALDER", simuleringstype)
            assertEquals("12906498357", pid)
            assertEquals("UGIF", sivilstand)
            assertTrue(harEps)
            assertEquals(100_000, sisteInntekt)
            assertEquals(1, uttaksar)
            assertEquals(DateFactory.date(2030, Calendar.MARCH), forsteUttaksdato) // 1963/1 + 67/1 + 0/1 = 2030/3
            assertEquals("P_80", uttaksgrad)
            assertEquals(12_000, inntektUnderGradertUttak)
            assertEquals(DateFactory.date(2032, Calendar.JANUARY), heltUttakDato) // 1963/1 + 68/11 + 0/1 = 2032/1
        }
    }

    private companion object {
        private val foedselDato = LocalDate.of(1963, 1, 1)

        private fun impersonalSpec() =
            ImpersonalSimuleringSpec(
                simuleringType = SimuleringType.ALDERSPENSJON,
                epsHarInntektOver2G = true,
                gradertUttak = GradertUttak(
                    grad = Uttaksgrad.AATTI_PROSENT,
                    uttakFomAlder = Alder(67, 1),
                    aarligInntekt = 12_000,
                    foedselDato = foedselDato
                ),
                heltUttak = HeltUttak(
                    uttakFomAlder = Alder(68, 11),
                    inntekt = null,
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
