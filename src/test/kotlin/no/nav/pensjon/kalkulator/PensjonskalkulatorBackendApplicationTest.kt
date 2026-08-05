package no.nav.pensjon.kalkulator

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PensjonskalkulatorBackendApplicationTest : StringSpec({

    "context loads" { }
}) {

    @MockkBean(name = "token-x-provider")
    private lateinit var tokenXProvider: AuthenticationProvider

    @MockkBean(name = "entra-id-provider")
    private lateinit var entraIdProvider: AuthenticationProvider
}
