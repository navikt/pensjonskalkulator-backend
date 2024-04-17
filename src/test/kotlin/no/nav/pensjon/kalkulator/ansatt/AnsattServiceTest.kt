package no.nav.pensjon.kalkulator.ansatt

import no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit.SecurityContextNavIdExtractor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class AnsattServiceTest {

    @Mock
    private lateinit var ansattIdExtractor: SecurityContextNavIdExtractor

    @Test
    fun getAnsattId() {
        `when`(ansattIdExtractor.id()).thenReturn("id1")
        assertEquals("id1", AnsattService(ansattIdExtractor).getAnsattId())
    }
}
