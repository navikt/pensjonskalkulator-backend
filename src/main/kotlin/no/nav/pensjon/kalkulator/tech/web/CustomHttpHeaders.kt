package no.nav.pensjon.kalkulator.tech.web

object CustomHttpHeaders {
    const val BEHANDLINGSNUMMER = "behandlingsnummer"
    const val CALL_ID = "Nav-Call-Id"
    const val CORRELATION_ID = "X-CORRELATION-ID" // required by Storebrand
    const val NORSK_PENSJON_CORRELATION_ID = "Correlation-Id" // required by Norsk Pensjon
    const val ORGANIZATION_NUMBER = "Organization-Number" // required by Norsk Pensjon
    const val DATE = "date"
    const val FULLMAKT_GIVER_PID = "fullmaktsgiverPid" // PID = person identifier
    const val PID = "fnr" // fødselsnummer
    const val PERSON_ID = "pid"
    const val SAMBOER_PID = "pidSamboer"
    const val THEME = "Tema"
}
