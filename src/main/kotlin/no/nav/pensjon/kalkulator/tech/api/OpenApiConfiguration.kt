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
                    .description("Tjenester for estimering av pensjonsutbetaling og innhenting av grunnlagsdata")
                    .version("v1.8.0")
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
                "/api/v9/alderspensjon/simulering",
                "/api/v8/alderspensjon/simulering", // deprecated
                "/api/v7/alderspensjon/simulering", // deprecated
                "/api/v6/alderspensjon/simulering", // deprecated
                "/api/v1/alderspensjon/anonym-simulering",
                "/api/v3/pensjonsavtaler",
                "/api/v2/pensjonsavtaler",
                "/api/v1/loepende-omstillingsstoenad-eller-gjenlevendeytelse",
                "/api/v4/vedtak/loepende-vedtak",
                "/api/v1/ekskludert",
                "/api/v2/ekskludert",
                "/api/v1/er-apoteker",
                "/api/v1/land-liste",
                "/api/v6/person",
                "/api/v1/nyligste-eps",
                "/api/v1/ansatt-id",
                "/api/v1/encrypt",
                "/api/v1/decrypt",
                "/api/v3/tidligste-hel-uttaksalder",
                "/api/v2/tidligste-hel-uttaksalder", // deprecated
                "/api/v1/ufoeregrad",
                "/api/feature/**",
                "/api/inntekt",
                "/api/intern/v1/pensjon/simulering",
                "/api/sak-status",
                "/api/tpo-medlemskap",
                "/api/v1/tpo-medlemskap",
                "/api/v2/tpo-livsvarig-offentlig-afp",
                "/api/v3/tpo-livsvarig-offentlig-afp",
                "/api/v2/simuler-oftp",
                "/api/v2/simuler-oftp/foer-1963",
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
