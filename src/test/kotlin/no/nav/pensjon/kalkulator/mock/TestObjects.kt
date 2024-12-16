package no.nav.pensjon.kalkulator.mock

import no.nav.pensjon.kalkulator.person.Pid
import org.springframework.security.oauth2.jwt.Jwt

object TestObjects {
    val jwt = Jwt("j.w.t", null, null, mapOf("k" to "v"), mapOf("k" to "v"))

    val pid1 = Pid("22925399748")
}
