package no.nav.pensjon.kalkulator.tech.ssl

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("norsk-pensjon.mtls")
class NorskPensjonMtlsProperties {
    var enabled: Boolean = false
    var secret: Secret = Secret()

    class Secret {
        var projectId: String = ""
        var credentialsSecretId: String = ""
        var credentialsVersion: String = "latest"
        var keyStoreSecretId: String = ""
        var keyStoreVersion: String = "latest"

        fun credentialsResourceName(): String =
            resourceName("credentials", credentialsSecretId, credentialsVersion)

        fun keyStoreResourceName(): String =
            resourceName("PKCS#12", keyStoreSecretId, keyStoreVersion)

        private fun resourceName(description: String, secretId: String, version: String): String {
            requireResourcePart("project ID", projectId)
            requireResourcePart("$description secret ID", secretId)
            requireResourcePart("$description secret version", version)
            return "projects/$projectId/secrets/$secretId/versions/$version"
        }

        private fun requireResourcePart(name: String, value: String) {
            require(value.isNotBlank() && '/' !in value) {
                "Norsk Pensjon certificate $name must be configured and cannot contain '/'"
            }
        }
    }
}
