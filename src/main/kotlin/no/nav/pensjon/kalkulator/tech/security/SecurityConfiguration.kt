package no.nav.pensjon.kalkulator.tech.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { registry ->
                registry
                    .requestMatchers(HttpMethod.GET, "/internal/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { configurer -> configurer.jwt() }
            .build()
    }
}
