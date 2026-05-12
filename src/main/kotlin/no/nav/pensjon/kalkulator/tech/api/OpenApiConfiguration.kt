package no.nav.pensjon.kalkulator.tech.api

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
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
            .components(
                Components()
                    .addSecuritySchemes(
                        "BearerAuthentication",
                        SecurityScheme()
                            .name("BearerAuthentication")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Nav-intern informasjon:\\\nFor å anskaffe et token i dev kan du bruke [tokenx-token-generator](https://tokenx-token-generator.intern.dev.nav.no/api/obo?aud=dev-gcp:pensjonskalkulator:pensjonskalkulator-backend)")
                    )
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
                "/api/intern/v1/enheter",
                "/api/intern/v1/eps",
                "/api/intern/v1/lagre-simulering",
                "/api/intern/v1/pensjon/simulering",
                "/api/intern/v1/person",
                "/api/intern/v1/sivilstatus",
                "/api/ekstern/v1/pensjon/simulering",
                "/api/v9/alderspensjon/simulering",
                "/api/v8/alderspensjon/simulering", // deprecated
                "/api/v7/alderspensjon/simulering", // deprecated
                "/api/v6/alderspensjon/simulering", // deprecated
                "/api/v1/alderspensjon/anonym-simulering",
                "/api/v3/pensjonsavtaler",
                "/api/v2/pensjonsavtaler", // deprecated
                "/api/v1/loepende-omstillingsstoenad-eller-gjenlevendeytelse",
                "/api/v1/vedtak",
                "/api/v4/vedtak/loepende-vedtak", // deprecated - replaced by /api/v1/vedtak
                "/api/v1/ekskludert", // deprecated
                "/api/v2/ekskludert",
                "/api/v1/er-apoteker",
                "/api/v1/land-liste",
                "/api/v7/person",
                "/api/v6/person", // deprecated
                "/api/v1/ansatt-id",
                "/api/v1/encrypt",
                "/api/v1/decrypt",
                "/api/v3/tidligste-hel-uttaksalder",
                "/api/v1/ufoeregrad",
                "/api/feature/**",
                "/api/inntekt",
                "/api/sak-status",
                "/api/tpo-medlemskap",
                "/api/v1/tpo-medlemskap",
                "/api/v2/tpo-livsvarig-offentlig-afp", // deprecated
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
