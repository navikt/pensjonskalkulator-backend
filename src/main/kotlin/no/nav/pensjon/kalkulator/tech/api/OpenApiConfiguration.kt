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
                    .version("v1.6.0")
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
                "/api/v8/alderspensjon/simulering",
                "/api/v7/alderspensjon/simulering",
                "/api/v6/alderspensjon/simulering",
                "/api/v1/alderspensjon/anonym-simulering",
                "/api/v3/pensjonsavtaler",
                "/api/v2/pensjonsavtaler",
                "/api/v1/loepende-omstillingsstoenad-eller-gjenlevendeytelse",
                "/api/v4/vedtak/loepende-vedtak",
                "/api/v3/vedtak/loepende-vedtak",
                "/api/v2/vedtak/loepende-vedtak",
                "/api/v1/ekskludert",
                "/api/v2/ekskludert",
                "/api/v1/land-liste",
                "/api/v5/person",
                "/api/v4/person",
                "/api/v2/person",
                "/api/v1/ansatt-id",
                "/api/v1/encrypt",
                "/api/v2/tidligste-hel-uttaksalder",
                "/api/v1/tidligste-hel-uttaksalder",
                "/api/v1/ufoeregrad",
                "/api/feature/**",
                "/api/inntekt",
                "/api/sak-status",
                "/api/tpo-medlemskap",
                "/api/v1/tpo-medlemskap",
                "/api/v1/simuler-oftp",
                "/api/v2/simuler-oftp",
                "/api/v2/aldersgrense",
                "/api/v1/aldersgrense",
                "/api/status",
            )
            .build()
    }

    @Bean
    fun publicApiDeprecatedGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("deprecated")
            .pathsToMatch(
                "/api/v1/vedtak/loepende-vedtak",
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
