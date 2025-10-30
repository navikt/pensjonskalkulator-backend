package no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import jakarta.validation.constraints.NotBlank

/**
 * Config for en enkelt AFP offentlig livsvarig leverandør
 */
data class AfpOffentligLivsvarigLeverandoerConfig(
    @field:NotBlank
    val url: String,
    @field:NotBlank
    val name: String,  // Provider display name (e.g., "KLP", "SPK")
    val scope: String = "",
    val audience: String = "",
    val overstyrteTpNr: String = ""  // Comma-separated list of TP numbers that override the normal lookup
)

/**
 * Config for alle AFP offentlig livsvarig leverandører
 */
@Component
@ConfigurationProperties(prefix = "livsvarig-afp-offentlig")
@Validated
data class AfpOffentligLivsvarigProperties(
    val tilbydere: Map<String, AfpOffentligLivsvarigLeverandoerConfig> = emptyMap()
)
