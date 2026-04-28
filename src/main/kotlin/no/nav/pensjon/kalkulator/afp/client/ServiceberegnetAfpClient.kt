package no.nav.pensjon.kalkulator.afp.client

import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpResult
import no.nav.pensjon.kalkulator.afp.ServiceberegnetAfpSpec

interface ServiceberegnetAfpClient {
    fun simulerServiceberegnetAfp(spec: ServiceberegnetAfpSpec): ServiceberegnetAfpResult
}
