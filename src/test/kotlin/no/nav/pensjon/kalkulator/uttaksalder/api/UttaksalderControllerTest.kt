package no.nav.pensjon.kalkulator.uttaksalder.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import no.nav.pensjon.kalkulator.uttaksalder.api.dto.UttaksalderIngressSpecDto
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UttaksalderController::class)
@Import(MockSecurityConfiguration::class)
internal class UttaksalderControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var service: UttaksalderService

    @MockBean
    private lateinit var traceAid: TraceAid

    @Test
    fun `finnTidligsteUttaksalder version 0`() {
        `when`(service.finnTidligsteUttaksalder(anyObject())).thenReturn(uttaksalder)

        mvc.perform(
            post("/api/tidligste-uttaksalder")
                .with(csrf())
                .content(requestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(responseBodyV0()))
    }

    @Test
    fun `finnTidligsteUttaksalder version 1`() {
        val spec = UttaksalderIngressSpecDto(Sivilstand.UGIFT, true, 100_000)
        `when`(service.finnTidligsteUttaksalder(spec)).thenReturn(uttaksalder)

        mvc.perform(
            post("/api/v1/tidligste-uttaksalder")
                .with(csrf())
                .content(requestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(responseBodyV1()))
    }

    @Language("json")
    private fun requestBody(
        sivilstand: Sivilstand = Sivilstand.UGIFT,
        harEps: Boolean = true,
        sisteInntekt: Int = 100_000,
    ): String = """
            {
              "sivilstand": "$sivilstand",
              "harEps": $harEps,
              "sisteInntekt": $sisteInntekt
            }
        """.trimIndent()

    @Language("json")
    private fun responseBodyV0(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaneder): String = """
            {
                "aar": $aar,
                "maaned": ${maaned + 1}
            }
        """.trimIndent()

    @Language("json")
    private fun responseBodyV1(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaneder): String = """
            {
                "aar": $aar,
                "maaneder": $maaned
            }
        """.trimIndent()

    private fun <T> anyObject(): T {
        return Mockito.any()
    }

    private companion object {
        private val uttaksalder = Alder(67, 10)
    }
}
