package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.mock.PersonFactory.person
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.opptjening.Inntekt
import no.nav.pensjon.kalkulator.opptjening.InntektService
import no.nav.pensjon.kalkulator.opptjening.Opptjeningstype
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.person.client.PersonClient
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.uttaksalder.client.UttaksalderClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
internal class UttaksalderServiceTest {

    private lateinit var service: UttaksalderService

    @Mock
    private lateinit var uttaksalderClient: UttaksalderClient

    @Mock
    private lateinit var inntektService: InntektService

    @Mock
    private lateinit var personClient: PersonClient

    @Mock
    private lateinit var pidGetter: PidGetter

    @BeforeEach
    fun initialize() {
        service = UttaksalderService(uttaksalderClient, inntektService, personClient, pidGetter)
        `when`(pidGetter.pid()).thenReturn(pid)
    }

    @Test
    fun `finnTidligsteUttaksalder uses properties from spec`() {
        val spec = ImpersonalUttaksalderSpec(Sivilstand.GIFT, true, 100_000, SimuleringType.ALDERSPENSJON,)

        service.finnTidligsteUttaksalder(spec)

        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(pid, Sivilstand.GIFT, true, 100_000, SimuleringType.ALDERSPENSJON)
        )
        verify(personClient, never()).fetchPerson(anyObject())
        verify(inntektService, never()).sistePensjonsgivendeInntekt()
    }

    @Test
    fun `finnTidligsteUttaksalder obtains inntekt and sivilstand when not specified`() {
        val person = person()
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid)).thenReturn(person)
        val spec = ImpersonalUttaksalderSpec(null, null, null, SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,)

        service.finnTidligsteUttaksalder(spec)

        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(
                pid = pid,
                sivilstand = person.sivilstand,
                harEps = false,
                sisteInntekt = inntekt.beloep.toInt(),
                simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
            )
        )
        verify(personClient, times(1)).fetchPerson(pid)
        verify(inntektService, times(1)).sistePensjonsgivendeInntekt()
    }

    @Test
    fun `when 'har EPS' and sivilstand not specified then service deduces it from person's sivilstand`() {
        val person = person(Sivilstand.SAMBOER)
        `when`(personClient.fetchPerson(pid)).thenReturn(person)
        val spec = ImpersonalUttaksalderSpec(
            sivilstand = null, // <----- sivilstand not specified
            harEps = null, // <----- 'har EPS' not specified
            sisteInntekt = 1,
            simuleringType = SimuleringType.ALDERSPENSJON,
        )

        service.finnTidligsteUttaksalder(spec)

        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(
                pid = pid,
                sivilstand = Sivilstand.SAMBOER,
                harEps = true, // <----- since person's sivilstand is 'samboer'
                sisteInntekt = 1,
                simuleringType = SimuleringType.ALDERSPENSJON
            )
        )
    }

    @Test
    fun `when 'har EPS' not specified then service deduces it from specified sivilstand`() {
        val person = person()
        `when`(personClient.fetchPerson(pid)).thenReturn(person)

        val spec = ImpersonalUttaksalderSpec(
            sivilstand = Sivilstand.REGISTRERT_PARTNER, // <----- sivilstand specified
            harEps = null, // <----- 'har EPS' not specified
            sisteInntekt = 1,
            simuleringType = SimuleringType.ALDERSPENSJON,
        )

        service.finnTidligsteUttaksalder(spec)

        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(
                pid = pid,
                sivilstand = Sivilstand.REGISTRERT_PARTNER,
                harEps = true, // <----- since specified sivilstand is 'registrert partner'
                sisteInntekt = 1,
                simuleringType = SimuleringType.ALDERSPENSJON
            )
        )
    }

    @Test
    fun `when 'har EPS' specified then service does not override it based on sivilstand`() {
        val person = person(Sivilstand.GIFT)
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid)).thenReturn(person)

        val spec = ImpersonalUttaksalderSpec(
            sivilstand = null,
            harEps = false, // <----- 'har EPS' specified
            sisteInntekt = null,
            simuleringType = SimuleringType.ALDERSPENSJON,
        )

        service.finnTidligsteUttaksalder(spec)

        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(
            UttaksalderSpec(
                pid = pid,
                sivilstand = Sivilstand.GIFT,
                harEps = false, // <----- not overridden (despite sivilstand 'gift')
                sisteInntekt = inntekt.beloep.toInt(),
                simuleringType = SimuleringType.ALDERSPENSJON
            )
        )
    }

    private fun <T> anyObject(): T {
        return any()
    }

    private companion object {
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
    }
}
