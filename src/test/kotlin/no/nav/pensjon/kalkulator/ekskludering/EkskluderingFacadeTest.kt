package no.nav.pensjon.kalkulator.ekskludering

import io.kotest.matchers.shouldBe
import no.nav.pensjon.kalkulator.sak.RelevantSakStatus
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType
import no.nav.pensjon.kalkulator.tjenestepensjon.TjenestepensjonService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class EkskluderingFacadeTest {

    @Mock
    private lateinit var sakService: SakService

    @Mock
    private lateinit var tjenestepensjonService: TjenestepensjonService

    private lateinit var ekskluderingService: EkskluderingFacade

    @BeforeEach
    fun initialize() {
        ekskluderingService = EkskluderingFacade(sakService, tjenestepensjonService)
    }

    @Test
    fun `'erEkskludert' gir normalt 'false'`() {
        val expected = EkskluderingStatus(false, EkskluderingAarsak.NONE)
        `when`(sakService.sakStatus()).thenReturn(RelevantSakStatus(false, SakType.NONE))
        `when`(tjenestepensjonService.erApoteker()).thenReturn(false)

        val erEkskludert = ekskluderingService.erEkskludert()

        erEkskludert shouldBe expected
    }

    @Test
    fun `'erEkskludert' gir 'true' ved gjenlevendeytelse`() {
        val expected = EkskluderingStatus(true, EkskluderingAarsak.HAR_GJENLEVENDEYTELSE)
        `when`(sakService.sakStatus()).thenReturn(RelevantSakStatus(true, SakType.GJENLEVENDEYTELSE))
        `when`(tjenestepensjonService.erApoteker()).thenReturn(false)

        val erEkskludert = ekskluderingService.erEkskludert()

        erEkskludert shouldBe expected
    }

    @Test
    fun `'erEkskludert' gir 'true' for apoteker`() {
        val expected = EkskluderingStatus(true, EkskluderingAarsak.ER_APOTEKER)
        `when`(sakService.sakStatus()).thenReturn(RelevantSakStatus(false, SakType.NONE))
        `when`(tjenestepensjonService.erApoteker()).thenReturn(true)

        val erEkskludert = ekskluderingService.erEkskludert()

        erEkskludert shouldBe expected
    }
}
