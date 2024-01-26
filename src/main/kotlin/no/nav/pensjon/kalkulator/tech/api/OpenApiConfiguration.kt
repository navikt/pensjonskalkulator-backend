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
                    .version("v1.1.0")
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Confluence-dokumentasjon for pensjonskalkulator-backend")
                    .url("https://confluence.adeo.no/display/PEN/Pensjonskalkulator+backend")
            )
    }

    @Bean
    fun publicApiCurrentGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("current")
            .pathsToMatch(
                "/api/v2/alderspensjon/simulering",
                "/api/v2/pensjonsavtaler",
                "/api/v1/ekskludert",
                "/api/v1/person",
                "/api/v1/tidligste-gradert-uttaksalder",
                "/api/v1/tidligste-hel-uttaksalder",
                "/api/feature/**",
                "/api/inntekt",
                "/api/sak-status",
                "/api/tpo-medlemskap",
                "/api/status",
            )
            .build()
    }

    @Bean
    fun publicApiDeprecatedGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("deprecated")
            .pathsToMatch(
                "/api/v1/alderspensjon/simulering",
                "/api/v1/pensjonsavtaler",
                "/api/v2/tidligste-uttaksalder",
                "/api/grunnbeloep",
            )
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
