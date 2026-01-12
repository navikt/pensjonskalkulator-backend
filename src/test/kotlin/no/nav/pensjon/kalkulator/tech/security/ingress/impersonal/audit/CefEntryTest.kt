package no.nav.pensjon.kalkulator.tech.security.ingress.impersonal.audit

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import org.slf4j.event.Level

class CefEntryTest : ShouldSpec({

    should("format returns info formatted according to CEF spec") {
        CefEntry(
            timestamp = 123456789L,
            level = Level.INFO,
            deviceEventClassId = "audit:edit",
            name = "Hendelse",
            message = "Noe blir gjort",
            sourceUserId = "X123456",
            destinationUserId = "01023456789"
        ).format() shouldBe
                "CEF:0|pensjon|pensjonskalkulator-backend|1.0|audit:edit|Hendelse|INFO" +
                "|end=123456789 suid=X123456 duid=01023456789 msg=Noe blir gjort" +
                " flexString1Label=Decision flexString1=Permit"
    }
})
