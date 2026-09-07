package no.nav.pensjon.kalkulator.tech.ssl

import tools.jackson.databind.ObjectMapper

class NorskPensjonCredentialsLoader(
    private val objectMapper: ObjectMapper
) {
    fun load(payload: ByteArray): NorskPensjonKeyStoreCredentials {
        val dto = try {
            objectMapper.readValue(payload, CredentialsDto::class.java)
        } catch (exception: Exception) {
            throw CertificateMaterialException(
                "Norsk Pensjon credentials secret does not contain valid JSON",
                exception
            )
        }

        if (!dto.type.equals("pkcs12", ignoreCase = true)) {
            throw CertificateMaterialException("Norsk Pensjon credentials type must be 'pkcs12'")
        }
        if (dto.password.isNullOrEmpty()) {
            throw CertificateMaterialException("Norsk Pensjon credentials secret does not contain a password")
        }
        if (dto.alias.isNullOrBlank()) {
            throw CertificateMaterialException("Norsk Pensjon credentials secret does not contain an alias")
        }

        return NorskPensjonKeyStoreCredentials(
            password = dto.password!!.toCharArray(),
            alias = dto.alias!!
        )
    }

    private class CredentialsDto {
        var password: String? = null
        var alias: String? = null
        var type: String? = null
    }
}

class NorskPensjonKeyStoreCredentials(
    val password: CharArray,
    val alias: String
) {
    override fun toString() = "NorskPensjonKeyStoreCredentials(password=******, alias=$alias)"
}
