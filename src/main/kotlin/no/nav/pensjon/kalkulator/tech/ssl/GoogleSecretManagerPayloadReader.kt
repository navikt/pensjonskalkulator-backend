package no.nav.pensjon.kalkulator.tech.ssl

import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.StatusCode
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient

class GoogleSecretManagerPayloadReader(
    private val client: SecretManagerServiceClient
) : SecretPayloadReader {

    override fun read(resourceName: String): ByteArray =
        try {
            client.accessSecretVersion(resourceName).payload.data.toByteArray().also {
                check(it.isNotEmpty()) { "Secret Manager returned an empty certificate payload for $resourceName" }
            }
        } catch (exception: ApiException) {
            throw SecretPayloadException(message(exception.statusCode.code, resourceName), exception)
        }

    private fun message(code: StatusCode.Code, resourceName: String): String =
        when (code) {
            StatusCode.Code.NOT_FOUND -> "Norsk Pensjon certificate secret was not found: $resourceName"
            StatusCode.Code.PERMISSION_DENIED ->
                "Application is not authorized to access the Norsk Pensjon certificate secret: $resourceName"

            else -> "Failed to read the Norsk Pensjon certificate secret $resourceName (${code.name})"
        }
}

class SecretPayloadException(message: String, cause: Throwable) : IllegalStateException(message, cause)
