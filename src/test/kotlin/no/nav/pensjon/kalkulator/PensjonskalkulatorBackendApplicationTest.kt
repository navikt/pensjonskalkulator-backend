package no.nav.pensjon.kalkulator

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(
    properties = [
        "spring.ssl.bundle.pem.norsk-pensjon.keystore.certificate=classpath:test-cert.pem"
    ]
)
class PensjonskalkulatorBackendApplicationTest : StringSpec({

    "context loads" { }
}) {

    @MockkBean(name = "token-x-provider")
    private lateinit var tokenXProvider: AuthenticationProvider

    @MockkBean(name = "entra-id-provider")
    private lateinit var entraIdProvider: AuthenticationProvider

    @MockkBean(name = "norsk-pensjon-rest")
    private lateinit var norskPensjonRestClient: PensjonsavtaleClient
}
