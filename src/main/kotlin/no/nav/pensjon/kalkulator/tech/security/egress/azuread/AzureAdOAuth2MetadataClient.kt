package no.nav.pensjon.kalkulator.tech.security.egress.azuread

import no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config.OAuth2ConfigurationClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class AzureAdOAuth2MetadataClient(
    webClient: WebClient,
    @Value("\${azure-app.well-known-url}") configurationUrl: String
) : OAuth2ConfigurationClient(webClient, configurationUrl)
