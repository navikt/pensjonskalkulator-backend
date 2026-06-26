package no.nav.pensjon.kalkulator.tech.representasjon

import no.nav.pensjon.kalkulator.person.EncryptedPid
import no.nav.pensjon.kalkulator.person.FoedselsnummerUtil.redact
import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.person.PossiblyEncryptedPid
import no.nav.pensjon.kalkulator.tech.crypto.CryptoService
import no.nav.pensjon.kalkulator.tech.representasjon.client.RepresentasjonClient
import org.springframework.stereotype.Service

@Service
class RepresentasjonService(
    private val client: RepresentasjonClient,
    private val pidEncrypter: CryptoService
) {
    fun hasValidRepresentasjonsforhold(fullmaktsgiverPid: PossiblyEncryptedPid): Representasjon =
        client.hasValidRepresentasjonsforhold(fullmaktsgiverPid = encrypted(fullmaktsgiverPid))

    private fun encrypted(pid: PossiblyEncryptedPid): EncryptedPid =
        when {
            pid.isEncrypted -> EncryptedPid(pid)
            Pid(pid.value).isValid -> EncryptedPid(pidEncrypter.encrypt(pid.value))
            else -> throw IllegalArgumentException("PID is invalid: ${redact(pid.value)}")
        }
}