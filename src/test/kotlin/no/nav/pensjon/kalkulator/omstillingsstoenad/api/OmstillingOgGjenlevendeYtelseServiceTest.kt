package no.nav.pensjon.kalkulator.omstillingsstoenad.api

import kotlinx.coroutines.test.runTest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseService
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingsstoenadService
import no.nav.pensjon.kalkulator.omstillingsstoenad.OmstillingOgGjenlevendeYtelseServiceTest.Companion.now
import no.nav.pensjon.kalkulator.sak.SakService
import no.nav.pensjon.kalkulator.sak.SakType
import no.nav.pensjon.kalkulator.tech.security.egress.token.validation.TimeProvider
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.testutil.anyNonNull
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class OmstillingOgGjenlevendeYtelseServiceTest {

    private lateinit var service: OmstillingOgGjenlevendeYtelseService

    @Mock
    private lateinit var omstillingsstoenadService: OmstillingsstoenadService

    @Mock
    private lateinit var sakService: SakService

    @Mock
    private lateinit var pidGetter: PidGetter

    @Mock
    private lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun initialize() {
        Mockito.`when`(pidGetter.pid()).thenReturn(pid)
        Mockito.`when`(timeProvider.time()).thenReturn(now)
        service = OmstillingOgGjenlevendeYtelseService(omstillingsstoenadService, sakService)
    }

    @Test
    fun `bruker mottar omstillingsstoenad og gjenlevendeYtelse`() = runTest {
        Mockito.`when`(omstillingsstoenadService.mottarOmstillingsstoenad()).thenReturn(true)
        Mockito.`when`(sakService.harSakType(anyNonNull<SakType>())).thenReturn(true)

        val result = service.harLoependeSaker()

        assertTrue(result)
    }

    @Test
    fun `bruker har ingen loepende saker`() = runTest {
        Mockito.`when`(omstillingsstoenadService.mottarOmstillingsstoenad()).thenReturn(false)
        Mockito.`when`(sakService.harSakType(anyNonNull<SakType>())).thenReturn(false)

        val result = service.harLoependeSaker()

        assertFalse(result)
    }

    @Test
    fun `bruker mottar kun omstillingsstoenad`() = runTest {
        Mockito.`when`(omstillingsstoenadService.mottarOmstillingsstoenad()).thenReturn(true)
        Mockito.`when`(sakService.harSakType(anyNonNull<SakType>())).thenReturn(false)

        val result = service.harLoependeSaker()

        assertTrue(result)
    }

    @Test
    fun `bruker mottar kun gjenlevendeYtelse`() = runTest {
        Mockito.`when`(omstillingsstoenadService.mottarOmstillingsstoenad()).thenReturn(false)
        Mockito.`when`(sakService.harSakType(anyNonNull<SakType>())).thenReturn(true)

        val result = service.harLoependeSaker()

        assertTrue(result)
    }
}