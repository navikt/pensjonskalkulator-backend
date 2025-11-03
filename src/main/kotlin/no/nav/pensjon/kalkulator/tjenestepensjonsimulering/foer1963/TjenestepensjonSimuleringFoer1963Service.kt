package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringFoer1963Client
import org.springframework.stereotype.Service

@Service
class TjenestepensjonSimuleringFoer1963Service(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringFoer1963Client
) {
    fun hentTjenestepensjonSimulering(spec: SimuleringOffentligTjenestepensjonFoer1963Spec): OffentligTjenestepensjonSimuleringFoer1963Resultat {
        val pid = pidGetter.pid()

        if (pid.value == "08496023642") return MOCK1

        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request = spec, pid = pidGetter.pid())
    }

    companion object {
         val MOCK1 = OffentligTjenestepensjonSimuleringFoer1963Resultat(
            tpnr = "123456",
            navnOrdning = "Mock Ordning AS",
            utbetalingsperioder = listOf(
                UtbetalingsperiodeResultat(
                    datoFom = null,
                    datoTom = null,
                    grad = 100,
                    arligUtbetaling = 120000.0,
                    ytelsekode = "OFTP",
                    mangelfullSimuleringkode = null
                )
            )
        )
    }
}
