package no.nav.pensjon.kalkulator.uttaksalder.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.person.Sivilstand
import no.nav.pensjon.kalkulator.uttaksalder.Uttaksalder
import no.nav.pensjon.kalkulator.uttaksalder.UttaksalderService
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
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

@Disabled
@WebMvcTest(UttaksalderController::class)
@Import(MockSecurityConfiguration::class)
internal class UttaksalderControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var uttaksalderService: UttaksalderService

    @Test
    fun finnTidligsteUttaksalder() {
        `when`(uttaksalderService.finnTidligsteUttaksalder(anyObject())).thenReturn(uttaksalder)

        mvc.perform(
            post("/api/tidligste-uttaksalder")
                .with(csrf())
                .content(requestBody())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(responseBody()))
    }

    @Language("json")
    private fun requestBody(
        sivilstand: Sivilstand = Sivilstand.GIFT,
        harEps: Boolean = false,
        sisteInntekt: Int = 100_000,
    ): String = """
            {
              "sivilstand": "$sivilstand",
              "harEps": $harEps,
              "sisteInntekt": $sisteInntekt
            }
        """.trimIndent()

    @Language("json")
    private fun responseBody(aar: Int = uttaksalder.aar, maaned: Int = uttaksalder.maaned): String = """
            {
                "aar": $aar,
                "maaned": $maaned
            }
        """.trimIndent()

    private fun <T> anyObject(): T {
        return Mockito.any()
    }

    private companion object {
        private val uttaksalder = Uttaksalder(67, 10)
    }
}