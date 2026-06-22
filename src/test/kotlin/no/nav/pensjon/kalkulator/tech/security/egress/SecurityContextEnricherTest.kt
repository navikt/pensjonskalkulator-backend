package no.nav.pensjon.kalkulator.tech.security.egress

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.crypto.CryptoService
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonService
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonTarget
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentertRolle
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextEnricherTest : ShouldSpec({

    val tokenSuppliers = EgressTokenSuppliersByService(emptyMap())

    context("if PID in request header") {
        should("not get PID from security context") {
            val securityContextPidExtractor = mockk<SecurityContextPidExtractor>()

            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor,
                pidDecrypter = mockk(),
                representasjonService = mockk()
            ).enrichAuthentication(
                request = arrangeFoedselsnummer(pid.value), // => PID in the request header
                response = mockk()
            )

            verify { securityContextPidExtractor wasNot Called }
        }
    }

    context("if no PID in request header") {
        should("get PID from security context") {
            setSecurityContext(authentication = mockk())
            val securityContextPidExtractor = mockk<SecurityContextPidExtractor>(relaxed = true)

            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor,
                pidDecrypter = mockk(),
                representasjonService = mockk()
            ).enrichAuthentication(
                request = arrangeFoedselsnummer(value = null), // => no PID in the request header
                response = mockk()
            )

            verify(exactly = 1) { securityContextPidExtractor.pid() }
        }
    }

    should("decrypt encrypted PID") {
        setSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            securityContextPidExtractor = mockk(relaxed = true),
            pidDecrypter = arrangeDecryption(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummer(ENCRYPTED_PID),
            response = mockk()
        )

        securityContextTargetPid()?.value shouldBe PID
    }

    should("use plaintext PID if not encrypted") {
        setSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            securityContextPidExtractor = mockk(),
            pidDecrypter = mockk(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummer(PID), // not encrypted
            response = mockk()
        )

        securityContextTargetPid()?.value shouldBe PID
    }

    context("valid representasjon") {
        should("set target PID from plaintext OBO cookie") {
            setSecurityContext(authentication = mockk())

            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor = arrangeSecurityContextPidExtractor(),
                pidDecrypter = mockk(),
                representasjonService = arrange(Representasjon(isValid = true, fullmaktGiverNavn = "F. Giver"))
            ).enrichAuthentication(
                request = arrangeOnBehalfOfCookie(PID),
                response = mockk()
            )

            securityContextTargetPid()?.value shouldBe PID
        }

        should("set target PID from encrypted OBO cookie") {
            setSecurityContext(authentication = mockk())

            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor = arrangeSecurityContextPidExtractor(),
                pidDecrypter = arrangeDecryption(),
                representasjonService = arrange(Representasjon(isValid = true, fullmaktGiverNavn = "F. Giver"))
            ).enrichAuthentication(
                request = arrangeOnBehalfOfCookie(ENCRYPTED_PID),
                response = mockk()
            )

            securityContextTargetPid()?.value shouldBe PID
        }
    }

    context("invalid representasjon") {
        should("throw AccessDeniedException") {
            setSecurityContext(authentication = mockk())

            shouldThrow<org.springframework.security.access.AccessDeniedException> {
                SecurityContextEnricher(
                    tokenSuppliers,
                    securityContextPidExtractor = arrangeSecurityContextPidExtractor(),
                    pidDecrypter = mockk(),
                    representasjonService = arrange(Representasjon(isValid = false, fullmaktGiverNavn = ""))
                ).enrichAuthentication(
                    request = arrangeOnBehalfOfCookie(PID),
                    response = mockk()
                )
            }.message shouldBe "INVALID_REPRESENTASJON"

            securityContextTargetPid()?.value shouldBe null
        }
    }

    context("person under veiledning") {
        should("not check representasjonsforhold despite presence of OBO cookie") {
            setSecurityContext(
                authentication = EnrichedAuthentication(
                    initialAuth = mockk(),
                    egressTokenSuppliersByService = tokenSuppliers,
                    target = RepresentasjonTarget(pid, rolle = RepresentertRolle.UNDER_VEILEDNING)
                )
            )
            val securityContextPidExtractor = mockk<SecurityContextPidExtractor>(relaxed = true)
            val representasjonService = mockk<RepresentasjonService>(relaxed = true)

            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor,
                pidDecrypter = mockk(),
                representasjonService
            ).enrichAuthentication(
                request = arrangeOnBehalfOfCookieAndHeader(),
                response = mockk()
            )

            verify(exactly = 0) { representasjonService.hasValidRepresentasjonsforhold(any()) }
        }
    }
})

private const val PID = "12906498357"
private const val ENCRYPTED_PID = "contains.dot"

private fun setSecurityContext(authentication: Authentication) {
    SecurityContextHolder.setContext(SecurityContextImpl(authentication))
}

private fun securityContextTargetPid(): Pid? =
    SecurityContextHolder.getContext().authentication?.enriched()?.targetPid()

private fun arrangeFoedselsnummer(value: String?): HttpServletRequest =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { getHeader("fnr") } returns value
    }

private fun arrangeOnBehalfOfCookie(value: String): HttpServletRequest =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { cookies } returns listOf(Cookie("nav-obo", value)).toTypedArray()
    }

private fun arrangeOnBehalfOfCookieAndHeader(): HttpServletRequest =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { getHeader("fnr") } returns PID
        every { cookies } returns listOf(Cookie("nav-obo", PID)).toTypedArray()
    }

private fun arrange(representasjon: Representasjon): RepresentasjonService =
    mockk {
        every {
            hasValidRepresentasjonsforhold(fullmaktGiverPid = Pid(PID))
        } returns representasjon
    }

private fun arrangeDecryption(): CryptoService =
    mockk { every { decrypt(ENCRYPTED_PID) } returns PID }

private fun arrangeSecurityContextPidExtractor(): SecurityContextPidExtractor =
    mockk { every { pid() } returns null }