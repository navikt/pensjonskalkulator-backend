package no.nav.pensjon.kalkulator.tech.selftest

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
class SelfTestController(val selfTest: SelfTest) {

    @GetMapping(path = ["internal/selftest"])
    fun selfTest(request: HttpServletRequest): ResponseEntity<String> {
        val accept = request.getHeader(HttpHeaders.ACCEPT)

        return if (MediaType.APPLICATION_JSON_VALUE == accept) responseEntity(
            selfTest.performSelfTestAndReportAsJson(), MediaType.APPLICATION_JSON
        ) else responseEntity(
            selfTest.performSelfTestAndReportAsHtml(), MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8)
        )
    }

    companion object {
        private fun responseEntity(body: String, mediaType: MediaType): ResponseEntity<String> =
            ResponseEntity(body, contentTypeHeaders(mediaType), HttpStatus.OK)

        private fun contentTypeHeaders(mediaType: MediaType): MultiValueMap<String, String> {
            val headers: MultiValueMap<String, String> = HttpHeaders()
            headers.add(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            return headers
        }
    }
}
