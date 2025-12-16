package no.nav.pensjon.kalkulator.tjenestepensjon.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.tjenestepensjon.AfpOffentligLivsvarigResult
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjon
import no.nav.pensjon.kalkulator.tjenestepensjon.Tjenestepensjonsforhold
import java.time.LocalDate

interface TjenestepensjonClient {

    fun harTjenestepensjonsforhold(pid: Pid, dato: LocalDate): Boolean

    fun erApoteker(pid: Pid): Boolean

    fun tjenestepensjon(pid: Pid): Tjenestepensjon

    fun tjenestepensjonsforhold(pid: Pid) : Tjenestepensjonsforhold

    fun afpOffentligLivsvarigTpNummerListe(pid: Pid): List<String>

    fun hentAfpOffentligLivsvarigDetaljer(pid: Pid, tpNr: String, uttaksdato: LocalDate): AfpOffentligLivsvarigResult
}
