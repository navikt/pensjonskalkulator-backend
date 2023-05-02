package no.nav.pensjon.kalkulator.simulering.api.map

import no.nav.pensjon.kalkulator.person.Pid
import no.nav.pensjon.kalkulator.simulering.SimuleringSpec
import no.nav.pensjon.kalkulator.simulering.api.dto.SimuleringSpecDto

object SimuleringMapper {

    fun asSpec(dto: SimuleringSpecDto, pidValue: String) =
        SimuleringSpec(
            dto.simuleringstype,
            Pid(pidValue),
            dto.forventetInntekt,
            dto.uttaksgrad,
            dto.foersteUttaksdato,
            dto.sivilstand,
            dto.epsHarInntektOver2G
        )
}
