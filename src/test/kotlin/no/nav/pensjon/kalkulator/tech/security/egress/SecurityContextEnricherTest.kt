package no.nav.pensjon.kalkulator.tech.security.egress

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tech.crypto.PidEncryptionService
import no.nav.pensjon.kalkulator.tech.representasjon.Representasjon
import no.nav.pensjon.kalkulator.tech.representasjon.RepresentasjonService
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import no.nav.pensjon.kalkulator.tech.security.ingress.SecurityContextPidExtractor
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl

class SecurityContextEnricherTest : FunSpec({

    val tokenSuppliers = EgressTokenSuppliersByService(emptyMap())

    test("enrichAuthentication tolerates null authentication") {
        setSecurityContext(authentication = null)

        shouldNotThrowAny {
            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor = mockk(),
                pidDecrypter = mockk(),
                representasjonService = mockk()
            ).enrichAuthentication(request = mockk(), response = mockk())
        }
    }

    test("if PID in request header then enrichAuthentication does not get PID from security context") {
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

    test("if no PID in request header then enrichAuthentication gets PID from security context") {
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

    test("enrichAuthentication decrypts encrypted PID") {
        setSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            securityContextPidExtractor = mockk(),
            pidDecrypter = arrangeDecryption(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummer("encrypted.string.containing.dot"), // encrypted PID
            response = mockk()
        )

        securityContextTargetPid()?.value shouldBe "12906498357"
    }

    test("enrichAuthentication uses plaintext PID if not encrypted") {
        setSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            securityContextPidExtractor = mockk(),
            pidDecrypter = mockk(),
            representasjonService = mockk()
        ).enrichAuthentication(
            request = arrangeFoedselsnummer("12906498357"), // not encrypted
            response = mockk()
        )

        securityContextTargetPid()?.value shouldBe "12906498357"
    }

    test("enrichAuthentication sets target PID from OBO cookie if valid representasjon") {
        setSecurityContext(authentication = mockk())

        SecurityContextEnricher(
            tokenSuppliers,
            securityContextPidExtractor = arrangeSecurityContextPidExtractor(),
            pidDecrypter = mockk(),
            representasjonService = arrange(Representasjon(isValid = true, fullmaktGiverNavn = "F. Giver"))
        ).enrichAuthentication(
            request = arrangeOnBehalfOfCookie(),
            response = mockk()
        )

        securityContextTargetPid()?.value shouldBe "12906498357"
    }

    test("enrichAuthentication throws AccessDeniedException if invalid representasjon") {
        setSecurityContext(authentication = mockk())

        shouldThrow<org.springframework.security.access.AccessDeniedException> {
            SecurityContextEnricher(
                tokenSuppliers,
                securityContextPidExtractor = arrangeSecurityContextPidExtractor(),
                pidDecrypter = mockk(),
                representasjonService = arrange(Representasjon(isValid = false, fullmaktGiverNavn = ""))
            ).enrichAuthentication(
                request = arrangeOnBehalfOfCookie(),
                response = mockk()
            )
        }.message shouldBe "INVALID_REPRESENTASJON"

        securityContextTargetPid()?.value shouldBe null
    }
})

private fun setSecurityContext(authentication: Authentication?) {
    SecurityContextHolder.setContext(SecurityContextImpl(authentication))
}

private fun securityContextTargetPid() =
    SecurityContextHolder.getContext().authentication?.enriched()?.targetPid()

private fun arrangeFoedselsnummer(value: String?) =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { getHeader("fnr") } returns value
    }

private fun arrangeOnBehalfOfCookie() =
    mockk<HttpServletRequest>(relaxed = true).apply {
        every { cookies } returns listOf(Cookie("nav-obo", "12906498357")).toTypedArray()
    }

private fun arrange(representasjon: Representasjon) =
    mockk<RepresentasjonService>().apply {
        every { hasValidRepresentasjonsforhold(Pid("12906498357")) } returns representasjon
    }

private fun arrangeDecryption() =
    mockk<PidEncryptionService>().apply {
        every { decrypt("encrypted.string.containing.dot") } returns "12906498357"
    }

private fun arrangeSecurityContextPidExtractor(): SecurityContextPidExtractor =
    mockk<SecurityContextPidExtractor>().apply {
        every { pid() } returns null
    }
