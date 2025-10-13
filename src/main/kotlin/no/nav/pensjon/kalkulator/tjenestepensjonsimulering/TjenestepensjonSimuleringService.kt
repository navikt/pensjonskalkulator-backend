package no.nav.pensjon.kalkulator.tjenestepensjonsimulering

import no.nav.pensjon.kalkulator.ekskludering.EkskluderingFacade
import no.nav.pensjon.kalkulator.tech.security.ingress.PidGetter
import no.nav.pensjon.kalkulator.tjenestepensjonsimulering.client.TjenestepensjonSimuleringClient
import no.nav.pensjon.kalkulator.vedtak.LoependeVedtakService
import org.springframework.stereotype.Service

@Service
class TjenestepensjonSimuleringService(
    private val pidGetter: PidGetter,
    private val tjenestepensjonSimuleringClient: TjenestepensjonSimuleringClient,
    private val loependeVedtakService: LoependeVedtakService,
    private val ekskluderingFacade: EkskluderingFacade
) {
    fun hentTjenestepensjonSimulering(spec: SimuleringOffentligTjenestepensjonSpec): OffentligTjenestepensjonSimuleringsresultat {
        val loependeVedtak = loependeVedtakService.hentLoependeVedtak()
        val erApoteker = ekskluderingFacade.apotekerEkskludering().ekskludert
        val erFoedtFoer1963 = spec.foedselsdato.year < 1963
        val harUfoeretrygdEllerPre2025OffentligAfp = loependeVedtak.ufoeretrygd != null || loependeVedtak.pre2025OffentligAfp != null

        if (erApoteker || (erFoedtFoer1963 && harUfoeretrygdEllerPre2025OffentligAfp)) {
            return SPERRET_SIMULERINGSRESULTAT
        }

        return tjenestepensjonSimuleringClient.hentTjenestepensjonSimulering(request = spec, pid = pidGetter.pid())
    }

    companion object {
        private val SPERRET_SIMULERINGSRESULTAT = OffentligTjenestepensjonSimuleringsresultat(SimuleringsResultatStatus(ResultatType.IKKE_MEDLEM))
    }
}
