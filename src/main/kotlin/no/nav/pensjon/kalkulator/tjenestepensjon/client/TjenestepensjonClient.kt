package no.nav.pensjon.kalkulator.tjenestepensjon.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjonsforhold
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import java.time.LocalDate

interface TjenestepensjonClient {

    fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean

    fun erApoteker(pid: Pid): Boolean

    fun tjenestepensjon(pid: Pid): Tjenestepensjon

    fun tjenestepensjonsforhold(pid: Pid) : Tjenestepensjonsforhold

    // TODO: det burde ikke være mulig å få flere tp-numre, men vi trenger fortsatt en håndtering for det
    fun afpOffentligLivsvarigTpNummerListe(pid: Pid): List<String>

    // Bruker første tpNr til å slå opp detaljer i de ulike TP-ordning-endepunkter parallelt og mapper til status + beloep
    fun hentAfpOffentligLivsvarigDetaljer(pid: Pid, tpNr: String, uttaksdato: LocalDate): AfpOffentligLivsvarigResult
}
