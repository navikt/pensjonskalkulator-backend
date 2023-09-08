package no.nav.pensjon.kalkulator.uttaksalder.client.pen

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration.Companion.arrangeSecurityContext
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.mock.WebClientTest
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.tech.trace.CallIdGenerator
import no.nav.pensjon.kalkulator.tech.web.WebClientConfig
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderSpec
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PenUttaksalderClientTest : WebClientTest() {

    private lateinit var client: PenUttaksalderClient

    @Mock
    private lateinit var callIdGenerator: CallIdGenerator

    @BeforeEach
    fun initialize() {
        client = PenUttaksalderClient(baseUrl(), WebClientConfig().regularWebClient(), callIdGenerator, "1")
    }

    @Test
    fun `finnTidligsteUttaksalder returns aar and maaned`() {
        arrangeSecurityContext()
        arrange(okResponse())

        val response = client.finnTidligsteUttaksalder(spec())

        assertEquals(63, response?.aar)
        assertEquals(10, response?.maaned)
    }

    private companion object {

        @Language("json")
        private const val ALDER = """{
    "aar": 63,
    "maaned": 10
}"""
    }

    private fun spec() =
        UttaksalderSpec(
            pid = pid,
            sivilstand = Sivilstand.UGIFT,
            harEps = true,
            sisteInntekt = 80
        )

    private fun okResponse() = jsonResponse(HttpStatus.OK).setBody(ALDER)
}
