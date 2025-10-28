package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import jakarta.validation.constraints.NotBlank

/**
 * Config for en enkelt tjenestepensjon ordning
 */
data class TpOrdningConfig(
    @field:NotBlank
    val url: String,
    @field:NotBlank
    val name: String,  // Provider display name (e.g., "KLP", "SPK")
    val scope: String = "",
    val audience: String = "",
    val overstyrteTpNr: List<String> = emptyList()
)

/**
 * Config for alle tjenestepensjon ordninger av livsvarig afp offentlig
 */
@ConfigurationProperties(prefix = "livsvarig-afp-offentlig")
@Validated
data class TpOrdningProperties(
    val tilbydere: Map<String, TpOrdningConfig> = emptyMap()
)

