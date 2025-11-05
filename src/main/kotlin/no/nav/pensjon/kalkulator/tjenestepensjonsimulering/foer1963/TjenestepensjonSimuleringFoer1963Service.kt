package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.foer1963

import no.nav.pensjon.kalkulator.general.Alder
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringFoer1963Client
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TjenestepensjonSimuleringFoer1963Service(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringFoer1963Client
) {
    fun hentTjenestepensjonSimulering(spec: SimuleringOffentligTjenestepensjonFoer1963Spec): OffentligTjenestepensjonSimuleringFoer1963Resultat {
        val pid = pidGetter.pid()

        if (pid.value == "20466120767") return MOCK1
        if (pid.value == "25476113736") return MOCK2

        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request = spec, pid = pidGetter.pid())
    }

    companion object {
        val MOCK1 = OffentligTjenestepensjonSimuleringFoer1963Resultat(
            tpnr = "3010",
            navnOrdning = "Statens pensjonskasse",
            utbetalingsperioder = listOf(
                UtbetalingsperiodeResultat(
                    alderFom = Alder(65, 0),
                    alderTom = Alder(70, 0),
                    grad = 100,
                    arligUtbetaling = 700000.0,
                    ytelsekode = YtelseskodeFoer1963.AFP,
                    mangelfullSimuleringkode = null
                ),
                UtbetalingsperiodeResultat(
                    alderFom = Alder(62, 0),
                    alderTom = null,
                    grad = 100,
                    arligUtbetaling = 100000.0,
                    ytelsekode = YtelseskodeFoer1963.AP,
                    mangelfullSimuleringkode = null
                )
            )
        )
        val MOCK2 = OffentligTjenestepensjonSimuleringFoer1963Resultat(
            tpnr = "3010",
            navnOrdning = "Statens pensjonskasse",
            utbetalingsperioder = listOf(
                UtbetalingsperiodeResultat(
                    alderFom = Alder(65, 0),
                    alderTom = Alder(70, 0),
                    grad = 100,
                    arligUtbetaling = 1000.0,
                    ytelsekode = YtelseskodeFoer1963.AFP,
                    mangelfullSimuleringkode = null
                ),
                UtbetalingsperiodeResultat(
                    alderFom = Alder(62, 0),
                    alderTom = null,
                    grad = 100,
                    arligUtbetaling = 100000.0,
                    ytelsekode = YtelseskodeFoer1963.AP,
                    mangelfullSimuleringkode = null
                )
            )
        )
    }
}
