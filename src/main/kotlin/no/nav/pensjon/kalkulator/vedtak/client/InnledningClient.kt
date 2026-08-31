package no.nav.pensjon.kalkulator.vedtak.client

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.vedtak.Innledningsdata

interface InnledningClient {
    fun hentInnledningsdata(pid: Pid): Innledningsdata
}
