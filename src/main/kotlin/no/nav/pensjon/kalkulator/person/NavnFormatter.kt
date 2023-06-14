package no.nav.pensjon.kalkulator.person

object NavnFormatter {

    fun formatNavn(fornavn: String?, mellomnavn: String?, etternavn: String?): String {
        val navn1 = fornavn?.trim() ?: ""
        val navn2 = mellomnavn?.trim() ?: ""
        val navn3 = etternavn?.trim() ?: ""
        val fornavnAndMellomnavn = "$navn1 $navn2".trim()
        val fulltNavn = "$fornavnAndMellomnavn $navn3".trim()
        return formatNavn(fulltNavn)
    }

    fun formatNavn(navn: String): String {
        val builder = StringBuilder()
        var capitalize = true

        for (character in navn.toCharArray()) {
            capitalize = if (capitalize) {
                builder.append(character.uppercaseChar())
                false
            } else {
                builder.append(character.lowercaseChar())
                character == ' ' || character == '-'
            }
        }

        return builder.toString()
    }
}
