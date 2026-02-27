package no.nav.pensjon.kalkulator.tech.text

object TextRedacter {

    private val PERSON_ID = """\b[0-9]{11}\b""".toRegex()

    fun redact(text: String) =
        PERSON_ID.replace(text, "(!redacted)")
}
