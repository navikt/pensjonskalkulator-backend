package no.nav.pensjon.kalkulator.avtale

import no.nav.pensjon.kalkulator.avtale.api.dto.PensjonsavtaleSpecDto
import no.nav.pensjon.kalkulator.avtale.client.PensjonsavtaleClient
import no.nav.pensjon.kalkulator.avtale.client.np.PensjonsavtaleSpec
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import org.springframework.stereotype.Service

@Service
class PensjonsavtaleService(
    private val avtaleClient: PensjonsavtaleClient,
    private val pidGetter: PidGetter
) {
    fun fetchAvtaler(spec: PensjonsavtaleSpecDto): Pensjonsavtaler {
        return avtaleClient.fetchAvtaler(fromDto(spec))
    }

    private fun fromDto(dto: PensjonsavtaleSpecDto) =
        PensjonsavtaleSpec(
            pidGetter.pid(),
            dto.aarligInntektFoerUttak,
            dto.uttaksperiode,
            dto.antallInntektsaarEtterUttak
        )
}
