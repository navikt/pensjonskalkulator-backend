package no.nav.pensjon.kalkulator.tech.trace

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest

class TraceAidTest : ShouldSpec({

    context("funksjonen 'begin'") {
        should("generate call ID if none supplied") {
            val callIdGenerator: CallIdGenerator = mockk { every { newId() } returns "generated" }
            val request: HttpServletRequest = mockk { every { getHeader(any()) } returns null }

            val traceAid = TraceAid(callIdGenerator)

            traceAid.begin(request) // none supplied

            traceAid.callId() shouldBe "generated"
        }

        should("use supplied call ID") {
            val callIdGenerator: CallIdGenerator = mockk()
            val request: HttpServletRequest = mockk {
                every { getHeader("x_correlation-id") } returns null
                every { getHeader("Nav-Call-Id") } returns "supplied"
            }
            val traceAid = TraceAid(callIdGenerator)

            traceAid.begin(request)

            traceAid.callId() shouldBe "supplied"
            verify { callIdGenerator wasNot Called }
        }

        should("use supplied correlation ID") {
            val callIdGenerator: CallIdGenerator = mockk()
            val request: HttpServletRequest = mockk {
                every { getHeader("x_correlation-id") } returns "supplied"
                every { getHeader("Nav-Call-Id") } returns null
            }
            val traceAid = TraceAid(callIdGenerator)

            traceAid.begin(request)

            traceAid.callId() shouldBe "supplied"
            verify { callIdGenerator wasNot Called }
        }
    }
})