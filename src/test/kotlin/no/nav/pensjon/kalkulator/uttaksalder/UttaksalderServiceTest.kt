package no.nav.pensjon.kalkulator.uttaksalder

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.general.HeltUttak
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
    fun `finnTidligsteUttaksalder uses properties from impersonal spec`() {
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = Sivilstand.GIFT,
            harEps = true,
            aarligInntektFoerUttak = 100_000,
            heltUttak = HeltUttak(Alder(67, 0), null)
        )

        service.finnTidligsteUttaksalder(impersonalSpec)

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.GIFT, // from impersonal spec
            harEps = true, // from impersonal spec
            aarligInntektFoerUttak = 100_000 // from impersonal spec
        )
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(impersonalSpec, personalSpec)
        verify(personClient, never()).fetchPerson(anyObject())
        verify(inntektService, never()).sistePensjonsgivendeInntekt()
    }

    @Test
    fun `finnTidligsteUttaksalder obtains inntekt and sivilstand when not specified`() {
        val person = person()
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid)).thenReturn(person)
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT,
            sivilstand = null, // sivilstand not specified
            harEps = null,
            aarligInntektFoerUttak = null, // inntekt not specified
            heltUttak = HeltUttak(Alder(67, 0), null)
        )

        service.finnTidligsteUttaksalder(impersonalSpec)

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = person.sivilstand, // sivilstand obtained
            harEps = false,
            aarligInntektFoerUttak = 543210 // inntekt obtained
        )
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(impersonalSpec, personalSpec)
        verify(personClient, times(1)).fetchPerson(pid)
        verify(inntektService, times(1)).sistePensjonsgivendeInntekt()
    }

    @Test
    fun `when 'har EPS' and sivilstand not specified then service deduces it from person's sivilstand`() {
        val person = person(Sivilstand.SAMBOER) // 'har EPS' is true for samboer
        `when`(personClient.fetchPerson(pid)).thenReturn(person)
        val impersonalSpec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = null, // sivilstand not specified
            harEps = null, // 'har EPS' not specified
            aarligInntektFoerUttak = 1,
            heltUttak = HeltUttak(Alder(67, 0), null)
        )

        service.finnTidligsteUttaksalder(impersonalSpec)

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.SAMBOER,
            harEps = true, // since samboer
            aarligInntektFoerUttak = 1
        )
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(impersonalSpec, personalSpec)
    }

    @Test
    fun `when 'har EPS' not specified then service deduces it from specified sivilstand`() {
        val person = person()
        `when`(personClient.fetchPerson(pid)).thenReturn(person)
        val spec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = Sivilstand.REGISTRERT_PARTNER, // sivilstand specified
            harEps = null, // 'har EPS' not specified
            aarligInntektFoerUttak = 1,
            heltUttak = HeltUttak(Alder(67, 0), null)
        )

        service.finnTidligsteUttaksalder(spec)

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.REGISTRERT_PARTNER,
            harEps = true, // since specified sivilstand is 'registrert partner'
            aarligInntektFoerUttak = 1
        )
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(spec, personalSpec)
    }

    @Test
    fun `when 'har EPS' specified then service does not override it based on sivilstand`() {
        val person = person(Sivilstand.GIFT)
        `when`(inntektService.sistePensjonsgivendeInntekt()).thenReturn(inntekt)
        `when`(personClient.fetchPerson(pid)).thenReturn(person)
        val spec = ImpersonalUttaksalderSpec(
            simuleringType = SimuleringType.ALDERSPENSJON,
            sivilstand = null,
            harEps = false, // 'har EPS' specified
            aarligInntektFoerUttak = null,
            heltUttak = HeltUttak(Alder(67, 0), null)
        )

        service.finnTidligsteUttaksalder(spec)

        val personalSpec = PersonalUttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.GIFT,
            harEps = false, // not overridden (despite sivilstand 'gift')
            aarligInntektFoerUttak = 543210
        )
        verify(uttaksalderClient, times(1)).finnTidligsteUttaksalder(spec, personalSpec)
    }

    private fun <T> anyObject(): T {
        return any()
    }

    private companion object {
        private val inntekt = Inntekt(Opptjeningstype.SUM_PENSJONSGIVENDE_INNTEKT, 2023, BigDecimal("543210"))
    }
}
