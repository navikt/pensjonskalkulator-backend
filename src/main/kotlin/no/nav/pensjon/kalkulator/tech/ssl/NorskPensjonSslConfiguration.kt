package no.nav.pensjon.kalkulator.tech.ssl

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.ssl.SslBundleRegistrar
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.ssl.SslBundle
import org.springframework.boot.ssl.SslBundleKey
import org.springframework.boot.ssl.SslStoreBundle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ObjectMapper

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NorskPensjonMtlsProperties::class)
class NorskPensjonSslConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty("norsk-pensjon.mtls.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    fun secretManagerServiceClient(): SecretManagerServiceClient =
        SecretManagerServiceClient.create()

    @Bean
    @ConditionalOnProperty("norsk-pensjon.mtls.enabled", havingValue = "true")
    @ConditionalOnMissingBean
    fun secretPayloadReader(client: SecretManagerServiceClient): SecretPayloadReader =
        GoogleSecretManagerPayloadReader(client)

    @Bean
    fun pkcs12KeyStoreLoader() = Pkcs12KeyStoreLoader()

    @Bean
    fun norskPensjonCredentialsLoader(objectMapper: ObjectMapper) =
        NorskPensjonCredentialsLoader(objectMapper)

    @Bean
    fun norskPensjonSslBundleRegistrar(
        properties: NorskPensjonMtlsProperties,
        payloadReader: ObjectProvider<SecretPayloadReader>,
        keyStoreLoader: Pkcs12KeyStoreLoader,
        credentialsLoader: NorskPensjonCredentialsLoader
    ) = SslBundleRegistrar { registry ->
        val bundle = if (properties.enabled) {
            val reader = payloadReader.ifAvailable
                ?: error("Secret Manager payload reader is unavailable while Norsk Pensjon mTLS is enabled")
            val credentialsPayload = reader.read(properties.secret.credentialsResourceName())
            val credentials = try {
                credentialsLoader.load(credentialsPayload)
            } finally {
                credentialsPayload.fill(0)
            }

            try {
                val keyStorePayload = reader.read(properties.secret.keyStoreResourceName())
                val keyStore = try {
                    keyStoreLoader.load(keyStorePayload, credentials.password, credentials.alias)
                } finally {
                    keyStorePayload.fill(0)
                }
                val password = String(credentials.password)
                SslBundle.of(
                    SslStoreBundle.of(keyStore, password, null),
                    SslBundleKey.of(password, credentials.alias)
                )
            } finally {
                credentials.password.fill('\u0000')
            }
        } else {
            SslBundle.systemDefault()
        }
        registry.registerBundle(BUNDLE_NAME, bundle)
    }

    private companion object {
        const val BUNDLE_NAME = "norsk-pensjon"
    }
}
