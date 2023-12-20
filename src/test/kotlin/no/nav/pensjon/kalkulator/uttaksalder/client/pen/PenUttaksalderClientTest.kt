package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.simulering.SimuleringType
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.tech.web.EgressException
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
class PenUttaksalderClientTest : WebClientTest() {

    private lateinit var client: PenUttaksalderClient

    @Autowired
    private lateinit var webClientBuilder: WebClient.Builder

    @Mock
    private lateinit var traceAid: TraceAid

    @BeforeEach
    fun initialize() {
        client = PenUttaksalderClient(baseUrl(), webClientBuilder, traceAid, "1")
        arrangeSecurityContext()
    }

    /**
     * PEN returnerer måned som i dato, mens vi opererer med måneder som i alder.
     * Eksempel: Fødselsdato 15.1.1963 gir teoretisk første uttaksdato 1.2.2025.
     * Da er brukerens alder 62 år, 0 måneder (og 16 dager), og vi opererer følgelig med 62 år, 0 måneder.
     * PEN på sin side returnerer 62 år, 1 måned – siden uttaksdato-måned (februar) er måneden etter fødselsmåneden (januar).
     */
    @Test
    fun `finnTidligsteUttaksalder returns aar and maaneder`() {
        arrange(okResponse())

        val response = client.finnTidligsteUttaksalder(spec)

        assertEquals(62, response?.aar)
        assertEquals(0, response?.maaneder) // PEN-måned minus 1
    }

    @Test
    fun `finnTidligsteUttaksalder throws EgressException when response is non-Conflict 4xx`() {
        arrange(other4xxResponse())

        val exception = assertThrows<EgressException> { client.finnTidligsteUttaksalder(spec) }

        assertEquals(
            """{
    "feilmelding": "Søk etter første uttaksdato feilet - antall måneder: 30 | Cause: PERSONOPPLYSNINGER_KontrollerPersonDetaljKonsistensRS.RolleFomDatoErNull SOKER",
    "merknader": []
}""", exception.message
        )
        assertTrue(exception.isClientError)
        assertFalse(exception.isConflict)
    }

    @Test
    fun `finnTidligsteUttaksalder throws EgressException when response is 409 Conflict`() {
        arrange(conflictResponse())

        val exception = assertThrows<EgressException> { client.finnTidligsteUttaksalder(spec) }

        assertEquals(
            """{
    "feilmelding": "Søk etter første uttaksdato feilet - antall måneder: 30 | Cause: PERSONOPPLYSNINGER_KontrollerPersonDetaljKonsistensRS.RolleFomDatoErNull SOKER",
    "merknader": []
}""", exception.message
        )
        assertTrue(exception.isClientError)
        assertTrue(exception.isConflict)
    }

    private companion object {

        @Language("json")
        private const val PEN_ALDER = """{
    "aar": 62,
    "maaned": 1
}"""

        @Language("json")
        private const val PEN_ERROR = """{
    "feilmelding": "Søk etter første uttaksdato feilet - antall måneder: 30 | Cause: PERSONOPPLYSNINGER_KontrollerPersonDetaljKonsistensRS.RolleFomDatoErNull SOKER",
    "merknader": []
}"""

        private val spec =
            UttaksalderSpec(
                pid = pid,
                sivilstand = Sivilstand.UGIFT,
                harEps = true,
                sisteInntekt = 80,
                SimuleringType.ALDERSPENSJON_MED_AFP_PRIVAT
            )

        private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(PEN_ALDER)

        private fun conflictResponse() = jsonResponse(HttpStatus.CONFLICT).setBody(PEN_ERROR)

        private fun other4xxResponse() = jsonResponse(HttpStatus.GONE).setBody(PEN_ERROR)
    }
}
