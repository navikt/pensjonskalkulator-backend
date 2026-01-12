package no.nav.pensjon.kalkulator

import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PensjonskalkulatorBackendApplicationTest : StringSpec({

    "context loads" { }
})
