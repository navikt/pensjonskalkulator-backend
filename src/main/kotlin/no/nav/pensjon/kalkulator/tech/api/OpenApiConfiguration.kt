package no.nav.pensjon.kalkulator.tech.api

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of OpenAPI (formerly known as Swagger).
 */
@Configuration
class OpenApiConfiguration {

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("pensjonskalkulator API")
                    .description("Pensjonskalkulator for brukere født i 1963 eller senere")
                    .version("v0.2.0-SNAPSHOT")
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Confluence-dokumentasjon for pensjonskalkulator-backend")
                    .url("https://confluence.adeo.no/display/PEN/Pensjonskalkulator+backend")
            )
    }

    @Bean
    fun publicApiGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("api")
            .pathsToMatch("/api/**")
            .build()
    }

    @Bean
    fun internalApiGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("internal")
            .pathsToMatch("/internal/**")
            .build()
    }
}
