package no.nav.pensjon.kalkulator.tech.crypto.api

import no.nav.pensjon.kalkulator.mock.MockSecurityConfiguration
import no.nav.pensjon.kalkulator.tech.crypto.PidEncryptionService
import no.nav.pensjon.kalkulator.tech.security.ingress.PidExtractor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.Auditor
import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.group.GroupMembershipService
import no.nav.pensjon.kalkulator.tech.trace.TraceAid
import org.junit.jupiter.api.Test
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

@WebMvcTest(CryptoController::class)
@Import(MockSecurityConfiguration::class)
class CryptoControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var pidEncryptionService: PidEncryptionService

    @MockBean
    private lateinit var traceAid: TraceAid

    @MockBean
    private lateinit var pidExtractor: PidExtractor

    @MockBean
    private lateinit var groupMembershipService: GroupMembershipService

    @MockBean
    private lateinit var auditor: Auditor

    @Test
    fun encrypt() {
        `when`(pidEncryptionService.encrypt("abc")).thenReturn("xyz")

        mvc.perform(
            post(URL_V1)
                .with(csrf())
                .content("abc")
                .contentType(MediaType.TEXT_PLAIN)
        )
            .andExpect(status().isOk())
            .andExpect(content().string("xyz"))
    }

    private companion object {
        private const val URL_V1 = "/api/v1/encrypt"
    }
}
