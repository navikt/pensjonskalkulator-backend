package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.mock.PersonFactory.pid
import no.nav.pensjon.kalkulator.tech.security.egress.EnrichedAuthentication
import no.nav.pensjon.kalkulator.tech.security.egress.config.EgressTokenSuppliersByService
import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.SecurityFilterChain

@TestComponent
class MockSecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .build()
    }

    companion object {
        fun arrangeSecurityContext() {
            SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext())

            SecurityContextHolder.getContext().authentication = EnrichedAuthentication(
                TestingAuthenticationToken("TEST_USER", jwt()),
                EgressTokenSuppliersByService(mapOf()),
                pid
            )
        }

        private fun jwt() = Jwt("j.w.t", null, null, map(), map())

        private fun map() = mapOf(Pair("k", "v"))
    }
}
