package no.nav.pensjon.kalkulator.tech.security.ingress.jwt

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import tools.jackson.databind.json.JsonMapper

class RequestClaimExtractorTest : ShouldSpec({

    should("extractAuthorizationClaim extracts claim from Authorization header JWT") {
        var request = mockk<HttpServletRequest>().apply {
            every { getHeader("Authorization") } returns JWT
        }

        RequestClaimExtractor(JsonMapper.builder().build()).extractAuthorizationClaim(
            request,
            claimName = "idtyp"
        ) shouldBe "app"
    }
})

/**
 * JSON Web Token for testing.
 */
private const val JWT =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ikg5bmo1QU9Tc3dNcGhnMVNGeDdqYVYtbEI5dyJ9" +
            ".eyJhdWQiOiJmNDA3YmFiZC04ZDBmLTRhZmMtYTZlNy00NmYyODYxMjY3MGIiLCJpc3MiOiJodHRwczovL2xvZ2luLm1pY3Jvc29mdG9ubGluZS5jb20vOTY2YWM1NzItZjViNy00YmJlLWFhODgtYzc2NDE5YzBmODUxL3YyLjAiLCJpYXQiOjE3MjUyNjU0NTMsIm5iZiI6MTcyNTI2NTQ1MywiZXhwIjoxNzI1MjY5MzUzLCJhaW8iOiJBU1FBMi84WEFBQUF2WEpnZWlhMmd4TmgrYVgxbHF6TU9BdkVnSVEvZDl0YlhNc3Q3YzZ5dlJFPSIsImF6cCI6IjVmMjhjMTBmLWVjZDUtNDVlZi05NmI4LWRiNDMxMTgwMmU4OSIsImF6cGFjciI6IjEiLCJpZHR5cCI6ImFwcCIsIm9pZCI6ImZiZDEzYTBlLTBhNzQtNDc3ZC05NTNhLTlmNGI1MjBhMGU4ZSIsInJoIjoiMC5BVWNBY3NWcWxyZjF2a3VxaU1ka0djRDRVYjI2Ql9RUGpmeEtwdWRHOG9ZU1p3c05BUUEuIiwicm9sZXMiOlsiYWNjZXNzX2FzX2FwcGxpY2F0aW9uIl0sInN1YiI6ImZiZDEzYTBlLTBhNzQtNDc3ZC05NTNhLTlmNGI1MjBhMGU4ZSIsInRpZCI6Ijk2NmFjNTcyLWY1YjctNGJiZS1hYTg4LWM3NjQxOWMwZjg1MSIsInV0aSI6Im41VHZLZ0tMUUVhOGJWYzdwXzRRQUEiLCJ2ZXIiOiIyLjAiLCJhenBfbmFtZSI6ImRldi1nY3A6YXVyYTphenVyZS10b2tlbi1nZW5lcmF0b3IifQ" +
            ".QE9rgqYkDh2jdnmwslBdJEOZ-WqDhD0ZqonoMeqU3GAKWFXEtlX1tN8ETbDabg2J4L_W5Ziciy2vKSVJGwx-2XJ6hFXunICwalFCl9nZB18L5bE3Dc8RU5nyB2hj4MgOFfeabd9Tp7ThUrCCxpMkq7ovhFHBMecQkf0sGqtV9BkU8pFt58l1bSJTz8qTq_XAnNRxsxTt4fJSxnkFRbgS-lmVh9OTN6GrJ7f-9wXktrcLUMo7ZV4zbLX-7KsXzO3fapLoHC3zaF3uBuk8wEgzEBL5CJiffveIMi_Jitz11ZAtKK2rp96LFK2ggN2cLyRKHzMb11cufMQOXgvJYJ8y-Q"
