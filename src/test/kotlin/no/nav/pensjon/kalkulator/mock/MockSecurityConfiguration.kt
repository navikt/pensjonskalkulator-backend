package no.nav.pensjon.kalkulator.mock

import org.springframework.boot.test.context.TestComponent
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@TestComponent
class MockSecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .build()
    }
}
