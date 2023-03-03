package no.nav.pensjon.kalkulator.tech.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity,
                    @Value("\${request-matcher.internal}") internalRequestMatcher : String
    ): SecurityFilterChain {
        return http
            .authorizeHttpRequests { registry ->
                registry
                    .requestMatchers(
                        HttpMethod.GET,
                        internalRequestMatcher,
                        "/api/status",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { configurer -> configurer.jwt() }
            .build()
    }
}
