package no.nav.pensjon.kalkulator.tech.ssl

import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.StatusCode
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretPayload
import com.google.protobuf.ByteString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk

class GoogleSecretManagerPayloadReaderTest : FunSpec({
    val resourceName = "projects/project/secrets/certificate/versions/latest"

    test("returns binary secret payload") {
        val client = mockk<SecretManagerServiceClient> {
            every { accessSecretVersion(resourceName) } returns response(byteArrayOf(1, 2, 3))
        }

        GoogleSecretManagerPayloadReader(client).read(resourceName).contentEquals(byteArrayOf(1, 2, 3)) shouldBe true
    }

    test("maps missing secret to sanitized error") {
        val client = failingClient(resourceName, StatusCode.Code.NOT_FOUND)

        shouldThrow<SecretPayloadException> {
            GoogleSecretManagerPayloadReader(client).read(resourceName)
        }.message shouldContain "certificate secret was not found"
    }

    test("maps missing IAM access to sanitized error") {
        val client = failingClient(resourceName, StatusCode.Code.PERMISSION_DENIED)

        shouldThrow<SecretPayloadException> {
            GoogleSecretManagerPayloadReader(client).read(resourceName)
        }.message shouldContain "not authorized"
    }
})

private fun response(payload: ByteArray): AccessSecretVersionResponse =
    AccessSecretVersionResponse.newBuilder()
        .setPayload(SecretPayload.newBuilder().setData(ByteString.copyFrom(payload)))
        .build()

private fun failingClient(resourceName: String, code: StatusCode.Code): SecretManagerServiceClient =
    mockk {
        every { accessSecretVersion(resourceName) } throws ApiException(
            RuntimeException("sensitive provider message"),
            mockk { every { this@mockk.code } returns code },
            false
        )
    }
