package no.nav.pensjon.kalkulator.aldersgrense

import no.nav.pensjon.kalkulator.aldersgrense.api.dto.AldersgrenseSpec
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.uttaksalder.normalder.NormertPensjoneringsalderService
import no.nav.pensjon.kalkulator.uttaksalder.normalder.PensjoneringAldre
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class AldersgrenseServiceTest {

    @Mock
    private lateinit var normertPensjoneringsalderService: NormertPensjoneringsalderService

    @InjectMocks
    private lateinit var service: AldersgrenseService

    @Test
    fun `hentAldersgrenser converts foedselsdato to LocalDate and calls normertPensjoneringsalderService`() {
        val spec = AldersgrenseSpec(foedselsdato = 1963)
        val expectedDate = LocalDate.of(1963, 1, 1)
        val expectedAldre = PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAldersgrense = Alder(aar = 62, maaneder = 0)
        )

        `when`(normertPensjoneringsalderService.getAldre(expectedDate)).thenReturn(expectedAldre)

        val result = service.hentAldersgrenser(spec)

        assertEquals(expectedAldre, result)
    }

    @Test
    fun `hentAldersgrenser handles different birth years`() {
        val spec = AldersgrenseSpec(foedselsdato = 1970)
        val expectedDate = LocalDate.of(1970, 1, 1)
        val expectedAldre = PensjoneringAldre(
            normalder = Alder(aar = 67, maaneder = 0),
            nedreAldersgrense = Alder(aar = 62, maaneder = 0)
        )

        `when`(normertPensjoneringsalderService.getAldre(expectedDate)).thenReturn(expectedAldre)

        val result = service.hentAldersgrenser(spec)

        assertEquals(expectedAldre, result)
    }
}