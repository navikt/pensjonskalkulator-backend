package no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjon.client.tp.TpTjenestepensjonClient
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.fra1963.client.TjenestepensjonSimuleringClient
import org.springframework.stereotype.Service

@Service
class TjenestepensjonSimuleringService(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringClient,
    private val loependeVedtakService: LoependeVedtakService,
    private val ekskluderingFacade: EkskluderingFacade,
    private val tpclient: TpTjenestepensjonClient
) {
    fun hentTjenestepensjonSimulering(spec: SimuleringOffentligTjenestepensjonSpec): OffentligTjenestepensjonSimuleringsresultat {
        val loependeVedtak = loependeVedtakService.hentLoependeVedtak()
        val erApoteker = ekskluderingFacade.apotekerEkskludering().ekskludert
        val erFoedtFoer1963 = spec.foedselsdato.year < 1963
        val harUfoeretrygdEllerPre2025OffentligAfp = loependeVedtak.ufoeretrygd != null || loependeVedtak.pre2025OffentligAfp != null
        val pid = pidGetter.pid()

        if (erApoteker || (erFoedtFoer1963 && harUfoeretrygdEllerPre2025OffentligAfp)) {
            val tpForhold = tpclient.tjenestepensjonsforhold(pid)
            return OffentligTjenestepensjonSimuleringsresultat(
                simuleringsResultatStatus = SimuleringsResultatStatus(ResultatType.TP_ORDNING_STOETTES_IKKE),
                tpOrdninger = tpForhold.tpOrdninger
            )
        }

        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request = spec, pid = pid)
    }

}
